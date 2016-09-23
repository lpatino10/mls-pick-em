import scrapy

class MatchesSpider(scrapy.Spider):
    name = "matches"
    start_urls = [
        'http://matchcenter.mlssoccer.com/matches',
    ]

    def parse(self, response):
        filename = 'quotes-' + response.url.split("/")[-2] + '.html'
        print('RESULT:')
        for day in response.xpath('/html/body/div[@id="main-content"]/div/main/section/div[@data-reactid="174"]/div[@class="ml-matches-wrapper"]/div[@class="ml-day-wrapper clearfix"]'):
            for match in day.xpath('a'):
                starting_string = 'div/div[@class="sb-header clearfix"]/div[@class="sb-score-billboard"]'

                home_team_path = starting_string + '/div[@class="sb-team sb-home"]/div/div[@class="sb-club-name"]/span[@class="sb-club-name-full"]/text()'
                home_team = match.xpath(home_team_path).extract()

                away_team_path = starting_string + '/div[@class="sb-team sb-away"]/div/div[@class="sb-club-name"]/span[@class="sb-club-name-full"]/text()'
                away_team = match.xpath(away_team_path).extract()

                date_path = starting_string + '/div[@class="sb-match-info"]/div/div[@class="sb-match-datetime"]/div[@class="sb-match-date"]/text()'
                date = match.xpath(date_path).extract()

                time_path = starting_string + '/div[@class="sb-match-info"]/div/div[@class="sb-match-datetime"]/div[@class="sb-match-time"]/text()'
                time = match.xpath(time_path).extract()

                competition = match.xpath('div/div[@class="sb-header clearfix"]/div[@class="sb-match-comp"]/text()').extract()

                print 'Home: {0}  Away: {1}  Date: {2}  Time: {3}  Competition: {4}'.format(home_team, away_team, date, time, competition)
