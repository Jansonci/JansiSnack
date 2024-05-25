package handler

import (
	"github.com/arman-aminian/twitter-backend/model"
	"github.com/arman-aminian/twitter-backend/utils"
	"github.com/gorilla/websocket"
	"github.com/labstack/echo/v4"
	"github.com/pkg/errors"
	"net/http"
	"strconv"
	"strings"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true // 允许所有跨域请求
	},
}

func (h *Handler) GetArticle(c echo.Context) error {
	id := c.Param("id")
	t, err := h.articleStore.GetArticleById(&id)
	if err != nil {
		return c.JSON(http.StatusInternalServerError, utils.NewError(err))
	}
	if t == nil {
		return c.JSON(http.StatusNotFound, utils.NotFound())
	}
	return c.JSON(http.StatusOK, newArticleResponse(c, t))
}

func (h *Handler) GetArticles(c echo.Context) error {
	articles := &model.ArticleIdList{}
	err := c.Bind(articles)
	if err != nil {
		return c.JSON(http.StatusUnprocessableEntity, utils.NewError(err))
	}
	if len(articles.Articles) == 0 {
		return c.JSON(http.StatusBadRequest, utils.NewError(errors.New("nothing to search for")))
	}
	res, err := h.articleStore.GetArticles(articles.Articles)
	if err != nil {
		return c.JSON(http.StatusUnprocessableEntity, utils.NewError(err))
	}
	return c.JSON(http.StatusOK, newAllArticleResponse(c, res))
}

func (h *Handler) GetAllArticle(c echo.Context) error {
	articles, err := h.articleStore.GetAllArticles()
	if err != nil {
		return c.JSON(http.StatusInternalServerError, echo.Map{"error": err.Error()})
	}
	return c.JSON(http.StatusOK, newAllArticleResponse(c, articles))
}

func (h *Handler) HelloWebSocket(c echo.Context) error {
	ws, err := upgrader.Upgrade(c.Response(), c.Request(), nil)
	if err != nil {
		return err
	}
	defer func(ws *websocket.Conn) {
		err := ws.Close()
		if err != nil {

		}
	}(ws)

	for {
		// 读取客户端消息
		_, msg, err := ws.ReadMessage()
		c.Logger().Printf(string("Yes!!!!!!!!!!!!!!!!!!" + string(msg)))
		if err != nil {
			c.Logger().Error("Error reading message:", err)
			break
		}
		parts := strings.Split(string(msg), ",")
		// 处理消息，例如更新数据库
		newStatus, err := h.articleStore.CollectArticle(parts[0], parts[1], parts[2])
		if err != nil {
			return err
		}

		responseMsg := parts[1] + "/" + strconv.FormatBool(newStatus)
		c.Logger().Printf(responseMsg)

		// 发送消息回客户端
		err = ws.WriteMessage(websocket.TextMessage, []byte(responseMsg))
		if err != nil {
			c.Logger().Error("Error writing message:", err)
			break
		}

	}
	return nil
}

func (h *Handler) HelloWebSocket1(c echo.Context) error {
	ws, err := upgrader.Upgrade(c.Response(), c.Request(), nil)
	if err != nil {
		return err
	}
	go handleMessages(ws, c, h)
	return nil
}
func handleMessages(ws *websocket.Conn, c echo.Context, h *Handler) {
	defer func(ws *websocket.Conn) {
		err := ws.Close()
		if err != nil {

		}
	}(ws)
	for {
		_, msg, err := ws.ReadMessage()
		if err != nil {
			c.Logger().Error("Error reading message:", err)
			break
		}
		// 处理消息的代码
		parts := strings.Split(string(msg), ",")
		// 处理消息，例如更新数据库
		newStatus, err := h.articleStore.CollectArticle(parts[0], parts[1], parts[2])
		if err != nil {
		}

		responseMsg := parts[1] + "/" + strconv.FormatBool(newStatus)
		c.Logger().Printf(responseMsg)

		// 发送消息回客户端
		err = ws.WriteMessage(websocket.TextMessage, []byte(responseMsg))
		if err != nil {
			c.Logger().Error("Error writing message:", err)
			break
		}
	}
}
