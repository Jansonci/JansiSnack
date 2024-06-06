package handler

import (
	"github.com/arman-aminian/twitter-backend/router/middleware"
	"github.com/arman-aminian/twitter-backend/utils"
	"github.com/labstack/echo/v4"
)

const (
	articles = "/article"
)

func (h *Handler) Register(g *echo.Group) {
	globalMiddleware := middleware.Global(utils.JWTSecret)
	articleGlobal := g.Group(articles, globalMiddleware)
	articleGlobal.GET("/:id", h.GetArticle)
	articleGlobal.GET("/all", h.GetAllArticle)
	articleGlobal.POST("/collections", h.GetArticles)
	articleGlobal.Static("/pics", "media")
	articleGlobal.GET("/websocket", h.HelloWebSocket1)
}
