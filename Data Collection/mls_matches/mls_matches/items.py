# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy


class MatchItem(scrapy.Item):
    home_team = scrapy.Field()
    home_score = scrapy.Field()
    away_team = scrapy.Field()
    away_score = scrapy.Field()
    date = scrapy.Field()
    time = scrapy.Field()
