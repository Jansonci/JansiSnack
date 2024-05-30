package article

import (
	"github.com/arman-aminian/twitter-backend/model"
)

type Store interface {
	GetArticleById(id *string) (*model.Article, error)
	GetArticles(Articles []string) ([]*model.Article, error)
	GetAllArticles() ([]*model.Article, error)
	CollectArticle(user string, article string, behavior string) (bool, error)
}
