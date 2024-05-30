package handler

import (
	"github.com/arman-aminian/twitter-backend/article"
)

// handler结构是Echo框架的核心，也是用户直接使用的工具类，其实归根到底这个tweeter后端项目还是一个crud，其核心依旧是和数据库的交互，所以可以看到
// handler结构是有三个storage对象组成的。
type Handler struct {
	articleStore article.Store
}

func NewHandler(as article.Store) *Handler {
	return &Handler{
		articleStore: as,
	}
}
