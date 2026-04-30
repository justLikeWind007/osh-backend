#!/bin/bash

# ES 连接信息
ES_HOST="http://43.242.200.25:9200"
ES_USER="elastic:osh88888888"
ES_INDEX="osh_course_index"

echo "========================================"
echo "检查 Elasticsearch 数据"
echo "========================================"
echo ""

echo "1. 查看所有索引："
curl -u $ES_USER -X GET "$ES_HOST/_cat/indices?v" | grep -E "health|osh_course"
echo ""
echo ""

echo "2. 查看 $ES_INDEX 索引的文档数量："
curl -u $ES_USER -X GET "$ES_HOST/$ES_INDEX/_count?pretty"
echo ""

echo "3. 查看前 3 条数据："
curl -u $ES_USER -X GET "$ES_HOST/$ES_INDEX/_search?pretty&size=3&_source=courseId,title,status,createTime,updateTime"
echo ""

echo "4. 按状态统计："
curl -u $ES_USER -X GET "$ES_HOST/$ES_INDEX/_search?pretty&size=0" -H 'Content-Type: application/json' -d'
{
  "aggs": {
    "status_count": {
      "terms": {
        "field": "status"
      }
    }
  }
}'
echo ""

echo "========================================"
echo "检查完成"
echo "========================================"
