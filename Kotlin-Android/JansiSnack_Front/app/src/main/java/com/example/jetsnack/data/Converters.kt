/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jetsnack.data

import androidx.room.TypeConverter
import com.example.jetsnack.model.Markup
import com.example.jetsnack.model.Paragraph
import com.example.jetsnack.model.ParagraphType
import com.example.jetsnack.model.Publication
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

/**
 * Type converters to allow Room to reference complex data types.
 */
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        return gson.toJson(list)
    }


    @TypeConverter
    fun fromPublication(value: Publication?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPublication(value: String): Publication? {
        return gson.fromJson(value, Publication::class.java)
    }

    @TypeConverter
    fun fromMetadata(value: com.example.jetsnack.model.Metadata): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMetadata(value: String): com.example.jetsnack.model.Metadata {
        return gson.fromJson(value, com.example.jetsnack.model.Metadata::class.java)
    }

    @TypeConverter
    fun fromParagraphList(paragraphs: List<Paragraph>): String {
        return gson.toJson(paragraphs)
    }

    @TypeConverter
    fun toParagraphList(value: String): List<Paragraph> {
        // 解析 JSON 字符串为 JsonArray
        val jsonArray = JsonParser.parseString(value).asJsonArray
        val paragraphs = mutableListOf<Paragraph>()

        // 遍历数组，逐个解析 Paragraph
        jsonArray.forEach { jsonElement ->
            val jsonObject = jsonElement.asJsonObject.toString()
            val paragraph = fromParagraphJson(jsonObject) // 使用已有的单个 Paragraph 解析逻辑
            paragraphs.add(paragraph)
        }
        return paragraphs
    }
    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        val arrayType = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(value, arrayType)
    }

    @TypeConverter
    fun fromParagraphJson(value: String?): Paragraph {
        if (value == null) return Paragraph(ParagraphType.Text, "", emptyList())

        // 解析 JSON 字符串为 JsonObject
        val jsonObject = JsonParser.parseString(value).asJsonObject

        // 获取字段的值并解析成相应类型
        val type = ParagraphType.valueOf(jsonObject.get("type").asString)
        val text = jsonObject.get("text").asString

        // 检查 markups 字段
        val markupsJson = jsonObject.get("markups")
        val markups: List<Markup> = if (markupsJson != null && markupsJson.isJsonArray) {
            gson.fromJson(markupsJson, object : TypeToken<List<Markup>>() {}.type)
        } else {
            emptyList()
        }
        return Paragraph(type, text, markups)
    }

    @TypeConverter
    fun toParagraphJson(paragraph: Paragraph): String {
        return gson.toJson(paragraph)
    }
}
