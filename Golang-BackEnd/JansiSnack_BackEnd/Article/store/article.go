package store

import (
	"context"
	"fmt"
	"github.com/arman-aminian/twitter-backend/model"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"io/ioutil"
	"net/http"
	"strconv"
)

type ArticleStore struct {
	db *mongo.Collection
}

func NewArticleStore(db *mongo.Collection) *ArticleStore {
	return &ArticleStore{
		db: db,
	}
}

func (ts *ArticleStore) GetArticleById(id *string) (*model.Article, error) {
	var t model.Article
	err := ts.db.FindOne(context.TODO(), bson.M{"id": id}).Decode(&t)
	if err != nil {
		return nil, err
	}
	return &t, err
}

func (ts *ArticleStore) GetArticles(Articles []string) ([]*model.Article, error) {
	var result []*model.Article
	query := bson.M{"id": bson.M{"$in": Articles}}
	res, err := ts.db.Find(context.TODO(), query) // 这三段操作等效于mongodb的sql语句，需要一个query语句（声明操作内容）；一个主方法（Find）并指明数据库得到
	// 一个Cursor（迭代器），最后用这个迭代器生成查询结果。
	if err != nil {
		return nil, err
	}
	if err = res.All(context.TODO(), &result); err != nil {
		return nil, err
	}
	return result, err
}

func (ts *ArticleStore) GetAllArticles() ([]*model.Article, error) {
	var articles []*model.Article
	cur, err := ts.db.Find(context.TODO(), bson.M{})
	if err != nil {
		return nil, err
	}
	defer func(cur *mongo.Cursor, ctx context.Context) {
		err := cur.Close(ctx)
		if err != nil {

		}
	}(cur, context.TODO()) // 确保游标被关闭
	if err = cur.All(context.TODO(), &articles); err != nil {
		return nil, err
	}
	return articles, nil
}

func (ts *ArticleStore) CollectArticle(user string, _article string, behavior string) (bool, error) {
	var article struct {
		collectedBy []int64 `bson:"likes"`
	}
	filter := bson.M{"id": _article}
	if err := ts.db.FindOne(context.TODO(), filter).Decode(&article); err != nil {
		return false, err
	}

	newStatus, err := strconv.ParseBool(behavior)

	userID, err := strconv.ParseInt(user, 10, 64)
	if err != nil {
		return false, err
	}
	var update bson.M
	if newStatus {
		// 如果newStatus为true，则添加userID到collectedBy数组
		update = bson.M{"$addToSet": bson.M{"likes": userID}}
	} else {
		// 如果newStatus为false，则从collectedBy数组中删除userID
		update = bson.M{"$pull": bson.M{"likes": userID}}
	}
	_, err = ts.db.UpdateOne(context.TODO(), filter, update)

	url := fmt.Sprintf("http://localhost:81/users/addArticleCollections?userId=%s&articleId=%s&behavior=%s", user, _article, behavior)
	req, err := http.NewRequest("PUT", url, nil)
	if err != nil {
		fmt.Println("Error creating request:", err)
	}
	// 创建http.Client对象并发送请求
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("Error sending request:", err)
	}
	defer resp.Body.Close()

	// 读取响应内容
	_, err = ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println("Error reading response body:", err)
	}

	return newStatus, err
}
