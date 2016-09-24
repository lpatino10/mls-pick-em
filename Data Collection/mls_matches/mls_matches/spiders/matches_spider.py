import scrapy
import pyrebase
import configparser
from mls_matches.items import MatchItem

configParser = configparser.ConfigParser()
configParser.read('/Users/loganpatino/Documents/Android_Dev/AndroidStudioProjects/MyApplication/Data Collection/mls_matches/mls_matches/spiders/config.ini')
print configParser.sections()

config = {
    "apiKey": configParser.get('key', 'ApiKey'),
    "authDomain": "com.example.loganpatino.mlspickem",
    "databaseURL": "https://mls-pick-em.firebaseio.com/",
    "storageBucket": ""
}

firebase = pyrebase.initialize_app(config)

def convertToMilitaryTime(time):
    if ("PM" not in time):
        return time
    hourIndex = time.index(':')
    hour = int(time[:hourIndex]) + 12
    minute = time[hourIndex+1:hourIndex+3]
    newTime = str(hour) + ':' + minute
    return newTime

class MatchesSpider(scrapy.Spider):
    name = "matches"
    start_urls = [
        'http://matchcenter.mlssoccer.com/matches',
    ]

    def parse(self, response):

        db = firebase.database()
        match_week_data = []
        starting_string = 'div/div[@class="sb-header clearfix"]/div[@class="sb-score-billboard"]'

        for day in response.xpath('/html/body/div[@id="main-content"]/div/main/section/div[@data-reactid="174"]/div[@class="ml-matches-wrapper"]/div[@class="ml-day-wrapper clearfix"]'):

            for match in day.xpath('a'):

                competition = match.xpath('div/div[@class="sb-header clearfix"]/div[@class="sb-match-comp"]/text()').extract()[0]

                # we only want MLS games
                if (competition == 'MLS'):

                    home_team_path = starting_string + '/div[@class="sb-team sb-home"]/div[@class="sb-team-info"]/div[@class="sb-club-name"]/span[@class="sb-club-name-full"]/text()'
                    home_team = match.xpath(home_team_path).extract()[0]

                    home_score_path = starting_string + '/div[@class="sb-team sb-home"]/div[contains(@class, sb-score)]/text()'
                    home_score = 0
                    if match.xpath(home_score_path).extract():
                        home_score = match.xpath(home_score_path).extract()[0]

                    away_team_path = starting_string + '/div[@class="sb-team sb-away"]/div[@class="sb-team-info"]/div[@class="sb-club-name"]/span[@class="sb-club-name-full"]/text()'
                    away_team = match.xpath(away_team_path).extract()[0]

                    away_score_path = starting_string + '/div[@class="sb-team sb-away"]/div[contains(@class, sb-score)]/text()'
                    away_score = 0
                    if match.xpath(away_score_path).extract():
                        away_score = match.xpath(away_score_path).extract()[0]

                    date = ''
                    # if the scores are shown, the date moves to a different spot
                    if match.xpath(home_score_path).extract():
                        date_path = 'div/div[@class="sb-footer-wrapper"]/div/div[@class="sb-match-date"]/text()'
                        date = match.xpath(date_path).extract()[0]
                    else:
                        date_path = starting_string + '/div[@class="sb-match-info"]/div/div[@class="sb-match-datetime"]/div[@class="sb-match-date"]/text()'
                        date = match.xpath(date_path).extract()[0]

                    time = ''
                    # same as above with date
                    if match.xpath(home_score_path).extract():
                        time_path = 'div/div[@class="sb-footer-wrapper"]/div/div[@class="sb-match-time"]/text()'
                        time = match.xpath(time_path).extract()[0]
                    else:
                        time_path = starting_string + '/div[@class="sb-match-info"]/div/div[@class="sb-match-datetime"]/div[@class="sb-match-time"]/text()'
                        time = match.xpath(time_path).extract()[0]
                    time = convertToMilitaryTime(time)

                    match_data = {"away": away_team, "awayScore": away_score, "home": home_team, "homeScore": home_score, "date": date, "time": time}
                    match_week_data.append(match_data)

                    #print 'Home: {0}  Score: {1}  Away: {2}  Score: {3}  Date: {4}  Time: {5}  Competition: {6}'.format(home_team, home_score, away_team, away_score, date, time, competition)

        db.child('games').set(match_week_data)
