package main

import (
	"github.com/arman-aminian/twitter-backend/db"
	echoSwagger "github.com/swaggo/echo-swagger"
	"os"

	"github.com/arman-aminian/twitter-backend/handler"
	"github.com/arman-aminian/twitter-backend/router"
	"github.com/arman-aminian/twitter-backend/store"

	"log"
)

func main() {
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	r := router.New()                            // 新建Echo对象并添加一些基础中间件
	r.GET("/swagger/*", echoSwagger.WrapHandler) // 结合Swagger
	mongoClient, err := db.GetMongoClient()
	if err != nil {
		log.Fatal(err)
	}
	articlesDb := db.SetupArticlesDb(mongoClient)

	g := r.Group("")
	as := store.NewArticleStore(articlesDb)
	h := handler.NewHandler( /*us, ts, hs,*/ as) // 新建handler对象
	h.Register(g)
	// 将handler对象注入到Echo对象(r)中，让r获得所有的用户接口
	r.Logger.Fatal(r.Start("0.0.0.0:" + port))
}
