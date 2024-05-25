package handler

import (
	"github.com/arman-aminian/twitter-backend/model"
	"github.com/labstack/echo/v4"
)

// Response结构专为进行前后端交互而存在，在Echo框架中所有handler方法的返回值都只有error，真正返回数据需要在c.Json方法中携带Response对象
// 这有些类似于SSM中的modelAndView,是让前端读懂后端的直接工具。

type ArticleResponse struct {
	BusinessID   string            `json:"id,omitempty" bson:"id"`
	Title        string            `json:"title" bson:"title"`
	Subtitle     string            `json:"subtitle"`
	URL          string            `json:"url" bson:"url"`
	IsCollected  bool              `json:"isCollected" bson:"is_collected"`
	Publication  model.Publication `json:"publication" bson:"publication"`
	Metadata     model.Metadata    `json:"metadata" bson:"metadata"`
	Paragraphs   []model.Paragraph `json:"paragraphs" bson:"paragraphs"`
	ImageId      string            `json:"imageId" bson:"image_id"`
	ImageThumbId string            `json:"imageThumbId" bson:"image_thumb_id"`
	Likes        *[]int64          `json:"likes" bson:"likes"`
}

func newArticleResponse(c echo.Context, t *model.Article) *ArticleResponse {
	tr := new(ArticleResponse)
	tr.BusinessID = t.BusinessID
	tr.Paragraphs = t.Paragraphs
	tr.Publication = t.Publication
	tr.Metadata = t.Metadata
	tr.URL = t.URL
	tr.Title = t.Title
	tr.Subtitle = t.Subtitle
	tr.ImageId = t.ImageId
	tr.ImageThumbId = t.ImageThumbId
	tr.Likes = t.Likes
	tr.IsCollected = t.IsCollected
	return tr
}

func newAllArticleResponse(c echo.Context, t []*model.Article) []*ArticleResponse {
	var tr []*ArticleResponse
	for _, bm := range t {
		tr = append(tr, newArticleResponse(c, bm))
	}
	return tr
}
