package model

import "go.mongodb.org/mongo-driver/bson/primitive"

type Article struct {
	ID           primitive.ObjectID `json:"id,omitempty" bson:"_id"`
	BusinessID   string             `json:"business_id,omitempty" bson:"id"`
	Title        string             `json:"title" bson:"title"`
	Subtitle     string             `json:"subtitle" bson:"subtitle"`
	URL          string             `json:"url" bson:"url"`
	IsCollected  bool               `json:"is_collected" bson:"is_collected"`
	Publication  Publication        `json:"publication" bson:"publication"`
	Metadata     Metadata           `json:"metadata" bson:"metadata"`
	Paragraphs   []Paragraph        `json:"paragraphs" bson:"paragraphs"`
	ImageId      string             `json:"image_id" bson:"image_id"`
	ImageThumbId string             `json:"image_thumb_id" bson:"image_thumb_id"`
	Likes        *[]int64           `json:"likes" bson:"likes"`
}

type ArticleIdList struct {
	Articles []string `json:"articles"`
}

// MarkupType 定义 MarkupType 和 ParagraphType 为特定的字符串类型

type MarkupType string
type ParagraphType string

// 定义可能的 MarkupType 和 ParagraphType 的值
const (
	Link   MarkupType = "Link"
	Code   MarkupType = "Code"
	Italic MarkupType = "Italic"
	Bold   MarkupType = "Bold"

	Title     ParagraphType = "Title"
	Caption   ParagraphType = "Caption"
	Header    ParagraphType = "Header"
	Subhead   ParagraphType = "Subhead"
	Text      ParagraphType = "Text"
	CodeBlock ParagraphType = "CodeBlock"
	Quote     ParagraphType = "Quote"
	Bullet    ParagraphType = "Bullet"
)

type Metadata struct {
	Author          PostAuthor `json:"author" bson:"author"`
	Date            string     `json:"date" bson:"date"`
	ReadTimeMinutes int        `json:"readTimeMinutes" bson:"readTimeMinutes"`
}

type PostAuthor struct {
	Name string  `json:"name" bson:"name"`
	URL  *string `json:"url" bson:"url"`
}

type Publication struct {
	Name    string `json:"name" bson:"name"`
	LogoUrl string `json:"logoUrl" bson:"logoUrl"`
}

type Paragraph struct {
	Type    ParagraphType `json:"type" bson:"type"`
	Text    string        `json:"text" bson:"text"`
	Markups []Markup      `json:"markups" bson:"markups"`
}

type Markup struct {
	Type  MarkupType `json:"type" bson:"type"`
	Start int        `json:"start" bson:"start"`
	End   int        `json:"end" bson:"end"`
	Href  *string    `json:"href" bson:"href"`
}
