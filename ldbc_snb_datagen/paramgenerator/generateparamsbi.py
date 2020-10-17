#!/usr/bin/env python2

import calendar
import codecs
import os
import random
import time
from datetime import datetime,timedelta

import readfactors
from timeparameters import *

START_DATE=datetime.strptime("2010-01-01", "%Y-%m-%d")
END_DATE=datetime.strptime("2013-01-01", "%Y-%m-%d")

def format_date(date):
   return int(time.mktime(date.timetuple())*1000)


class ParamsWriter:
   def __init__(self, outdir, number, param_names):
      self.file = codecs.open(outdir+"/bi_"+str(number)+"_param.txt", "w",encoding="utf-8")
      for i in range(0,len(param_names)):
         if i>0:
            self.file.write("|")
         self.file.write(param_names[i])
      self.file.write("\n")

   def append(self, params):
      for i, param in enumerate(params):
         if i>0:
            self.file.write("|")
         self.file.write(param)
      self.file.write("\n")


def post_date_right_open_range_params(sample, lower_bound, upper_bound):
   results = []
   for ix in range(0, len(sample)):
      start_offset = sample[ix][0]
      count_sum = 0
      for offset, count in sample[ix:]:
         count_sum += count
      if count_sum > lower_bound and count_sum < upper_bound:
         results.append([start_offset, count_sum])
   return results

def post_date_range_params(sample, lower_bound, upper_bound):
   results = []
   for ix in range(0, len(sample)):
      start_offset = sample[ix][0]
      count_sum = 0
      for offset, count in sample[ix:]:
         count_sum += count
         if count_sum > lower_bound and count_sum < upper_bound:
            results.append([[start_offset, offset], count_sum])
   return results

def post_month_params(sample, lower_bound, upper_bound):
   results = []
   for ix in range(0, len(sample)/4):
      start_ix = ix*4
      count_sum = 0
      for offset, count in sample[start_ix:start_ix+4]:
         count_sum += count
      if count_sum > lower_bound and count_sum < upper_bound:
         start_day = sample[start_ix][0]
         end_day = sample[start_ix+4][0]
         results.append([[start_day, end_day], count_sum])
   return results

def enumerate_path_bounds(minLength,maxLength,minDifference):
  results = []
  for i in range(minLength, maxLength):
     for j in range(i+minDifference,maxLength):
        results.append([i,j])
  return results

def prob_language_codes():
  results = []
  results.append(["ar"])
  for i in range(0, 2):
     results.append(["tk"])
  for i in range(0, 8):
     results.append(["uz"])
  for i in range(0, 2):
     results.append(["uz","tk"])
  return results

def prob_post_lengths():
  results = [20,40,113,97,240]
  return results

def key_params(sample, lower_bound, upper_bound):
   results = []
   for key, count in sample:
      if count > lower_bound and count < upper_bound:
         results.append([key, count])
   return results

def serialize_q1(outdir, post_weeks):
   writer = ParamsWriter(outdir, 1, ["date"])
   for week, count in post_weeks:
      writer.append([str(week)])

def serialize_q2(outdir, countries, post_day_ranges):
   writer = ParamsWriter(outdir, 2, ["date1", "date2", "country1", "country2"])
   for day_range, count_post in post_day_ranges:
      for ix in range(0,len(countries)):
         country_1, count_1 = countries[ix]
         for country_2, count_2 in countries[ix+1:]:
            writer.append([str(day_range[0]),str(day_range[1]),country_1,country_2])

def serialize_q3(outdir, post_months):
   writer = ParamsWriter(outdir, 3, ["year", "month"] )
   for post_month in post_months:
      t = time.gmtime(post_month[0][0]/1000)
      writer.append([str(t.tm_year), str(t.tm_mon)])

def serialize_q4(outdir, tagclasses, countries):
   writer = ParamsWriter(outdir, 4, ["tagClass", "country"])
   for tag, count_a in tagclasses:
      for country, count_b in countries:
         writer.append([tag,country])

def serialize_q5(outdir, countries):
   writer = ParamsWriter(outdir, 5, ["country"])
   for country, count in countries:
      writer.append([country])


def serialize_q6(outdir, tags):
   writer = ParamsWriter(outdir, 6, ["tag"])
   for tag, count in tags:
      writer.append([tag])

def serialize_q7(outdir, tags):
   writer = ParamsWriter(outdir, 7, ["tag"])
   for tag, count in tags:
      writer.append([tag])

def serialize_q8(outdir, tags):
   writer = ParamsWriter(outdir, 8, ["tag"])
   for tag, count in tags:
      writer.append([tag])

def serialize_q9(outdir, tagclasses):
   writer = ParamsWriter(outdir, 9, ["tagClass1", "tagClass2", "threshold"])
   for ix in range(0,len(tagclasses)):
      tag_class_a, count_a = tagclasses[ix]
      for tag_class_b, count_b in tagclasses[ix+1:]:
         writer.append([tag_class_a, tag_class_b, str(200)])

def serialize_q10(outdir, tags, post_weeks):
   writer = ParamsWriter(outdir, 10, ["tag", "date"])
   for tag, count in tags:
      for week, count in post_weeks:
         writer.append([tag, str(week)])

def serialize_q11(outdir, countries, bad_words):
   writer = ParamsWriter(outdir, 11, ["country", "blacklist"])
   random.seed(1988+1)
   # note: this approach keeps shuffling the bad_words list
   for country, count in countries:
      num_words = random.randint(1,min(len(bad_words),4));
      random.shuffle(bad_words)
      blacklist = bad_words[0:num_words]
      writer.append([country,";".join(blacklist)])

      num_words = random.randint(1,min(len(bad_words),10));
      random.shuffle(bad_words)
      blacklist = bad_words[0:num_words]
      writer.append([country,";".join(blacklist)])

      num_words = random.randint(1,min(len(bad_words),7));
      random.shuffle(bad_words)
      blacklist = bad_words[0:num_words]
      writer.append([country,";".join(blacklist)])

def serialize_q12(outdir, post_weeks):
   writer = ParamsWriter(outdir, 12, ["date", "likeThreshold"])
   for week, count in post_weeks:
      writer.append([str(week),str(400)])

def serialize_q13(outdir, countries):
   writer = ParamsWriter(outdir, 13, ["country"])
   for country, count in countries:
      writer.append([country])

def serialize_q14(outdir, creationdates):
   writer = ParamsWriter(outdir, 14, ["startDate", "endDate"])
   for creation, count in creationdates:
      writer.append([str(creation[0]),str(creation[1])])

def serialize_q15(outdir, countries):
   writer = ParamsWriter(outdir, 15, ["country"])
   for country, count in countries:
      writer.append([country])

def serialize_q16(outdir, persons, tagclasses, countries, path_bounds):
   writer = ParamsWriter(outdir, 16, ["person", "country", "tagClass", "minPathDistance", "maxPathDistance"])
   random.seed(1988+2)
   for country, count_b in countries:
      for tagClass, count_a in tagclasses:
         for minDist, maxDist in path_bounds:
            writer.append([str(persons[random.randint(0, len(persons))]), country, tagClass, str(minDist), str(maxDist)])

def serialize_q17(outdir, countries):
   writer = ParamsWriter(outdir, 17, ["country"])
   for country, count in countries:
      writer.append([country])

def serialize_q18(outdir, post_weeks, lengths, languages):
   writer = ParamsWriter(outdir, 18, ["date", "lengthThreshold", "languages"])
   for week, count in post_weeks:
      for length in lengths:
         for language_set in languages:
            writer.append([str(week), str(length), ";".join(language_set)])

def serialize_q19(outdir, tagclasses):
   PERS_DATE=datetime.strptime("1989-1-1", "%Y-%m-%d")
   writer = ParamsWriter(outdir, 19, ["date", "tagClass1", "tagClass2"])
   for ix in range(0,len(tagclasses)):
      tag_class_a, count_a = tagclasses[ix]
      for tag_class_b, count_b in tagclasses[ix+1:]:
         writer.append([str(format_date(PERS_DATE)),tag_class_a, tag_class_b])

def serialize_q20(outdir, tagclasses):
   random.seed(1988+3)
   writer = ParamsWriter(outdir, 20, ["tagClasses"])

   tagclasses = [tc[0] for tc in tagclasses]

   # I'm not sure this is the correct way to approach this problem,
   # but it should work reasonably well
   num_words = random.randint(1,min(len(tagclasses),4));
   random.shuffle(tagclasses)
   tcs = tagclasses[0:num_words]
   writer.append([";".join(tcs)])

   num_words = random.randint(1,min(len(tagclasses),10));
   random.shuffle(tagclasses)
   tcs = tagclasses[0:num_words]
   writer.append([";".join(tcs)])

   num_words = random.randint(1,min(len(tagclasses),7));
   random.shuffle(tagclasses)
   tcs = tagclasses[0:num_words]
   writer.append([";".join(tcs)])

def serialize_q21(outdir, countries):
   writer = ParamsWriter(outdir, 21, ["country", "endDate"])
   for country, count in countries:
      writer.append([country,str(format_date(END_DATE))])

def serialize_q22(outdir, countries):
   writer = ParamsWriter(outdir, 22, ["country1", "country2"])
   for ix in range(0,len(countries)):
      country_a, count_a = countries[ix]
      for country_b, count_b in countries[ix+1:]:
         writer.append([country_a, country_b])

def serialize_q23(outdir, countries):
   writer = ParamsWriter(outdir, 23, ["country"])
   for country, count in countries:
      writer.append([country])

def serialize_q24(outdir, tagclasses):
   writer = ParamsWriter(outdir, 24, ["tagClass"])
   for tagclass, count in tagclasses:
      writer.append([tagclass])

def serialize_q25(outdir, persons, post_month_ranges):
   writer = ParamsWriter(outdir, 25, ["person1Id", "person2Id", "startDate", "endDate"])
   for day_range, count_post in post_month_ranges:
      count = min(len(persons), 10)
      for _ in range(0, count):
         person1Id = persons[random.randint(0, len(persons) - 1)]
         while True:
            person2Id = persons[random.randint(0, len(persons) - 1)]
            if person2Id != person1Id:
               writer.append([str(person1Id), str(person2Id), str(day_range[0]), str(day_range[1])])
               break


def add_months(sourcedate,months):
   month = sourcedate.month - 1 + months
   year = int(sourcedate.year + month / 12 )
   month = month % 12 + 1
   day = min(sourcedate.day,calendar.monthrange(year,month)[1])
   return sourcedate.replace(year, month, day)

def convert_posts_histo(histogram):
   week_posts = []
   month = 0
   while (histogram.existParam(month)):
      monthTotal = histogram.getValue(month, "p")
      baseDate=add_months(START_DATE,month)
      week_posts.append([format_date(baseDate), monthTotal/4])
      week_posts.append([format_date(baseDate+timedelta(days=7)), monthTotal/4])
      week_posts.append([format_date(baseDate+timedelta(days=14)), monthTotal/4])
      week_posts.append([format_date(baseDate+timedelta(days=21)), monthTotal/4])
      month = month + 1
   return week_posts

def main(argv=None):
   if argv is None:
      argv = sys.argv

   if len(argv) < 3:
      print "arguments: <input dir> <output dir>"
      return 1

   indir = argv[1]+"/"
   outdir = argv[2]+"/"
   activityFactorFiles=[]
   personFactorFiles=[]
   friendsFiles = []

   for file in os.listdir(indir):
      if file.endswith("activityFactors.txt"):
         activityFactorFiles.append(indir+file)
      if file.endswith("personFactors.txt"):
         personFactorFiles.append(indir+file)
      if file.startswith("m0friendList"):
         friendsFiles.append(indir+file)

   # read precomputed counts from files   
   (personFactors, countryFactors, tagFactors, tagClassFactors, nameFactors, givenNames,  ts, postsHisto) = \
      readfactors.load(personFactorFiles,activityFactorFiles, friendsFiles)
   week_posts = convert_posts_histo(postsHisto)

   persons = []
   for key, _ in personFactors.values.iteritems():
      persons.append(key)
   random.seed(1988)
   random.shuffle(persons)

   country_sample = []
   for key, value in countryFactors.values.iteritems():
      country_sample.append([key, value.getValue("p")])
   country_sample.sort(key=lambda x: x[1], reverse=True)

   tagclass_posts = tagClassFactors
   tagclass_posts.sort(key=lambda x: x[1], reverse=True)

   tag_posts = tagFactors
   tag_posts.sort(key=lambda x: x[1], reverse=True)

   total_posts = 0
   for day, count in tag_posts:
      total_posts += count

   person_sum = 0
   for country, count in country_sample:
      person_sum += count

   post_lower_threshold = 0.1*total_posts*0.9
   post_upper_threshold = 0.1*total_posts*1.1
   post_day_ranges = post_date_range_params(week_posts, post_lower_threshold, post_upper_threshold)
   
   bad_words = ['Augustine','William','James','with','Henry','Robert','from','Pope','Hippo','album','David','has','one','also','Green','which','that']
   #post_lower_threshold = (total_posts/(week_posts[len(week_posts)-1][0]/7/4))*0.8
   #post_upper_threshold = (total_posts/(week_posts[len(week_posts)-1][0]/7/4))*1.2
   non_empty_weeks=len(week_posts)
   for ix in range(0,len(week_posts)):
      if week_posts[ix][1]==0:
         non_empty_weeks-= 1

   post_lower_threshold = (total_posts/(non_empty_weeks/4))*0.8
   post_upper_threshold = (total_posts/(non_empty_weeks/4))*1.2
   post_months = post_month_params(week_posts, post_lower_threshold, post_upper_threshold)

   # the lower bound is inclusive and the upper bound is exclusive
   path_bounds = enumerate_path_bounds(3, 6, 2)
   language_codes = prob_language_codes()
   post_lengths = prob_post_lengths()

   serialize_q2 (outdir, key_params(country_sample, total_posts/200, total_posts/100), post_day_ranges) # TODO determine constants
   serialize_q3 (outdir, post_months)
   serialize_q14(outdir, post_months)

   serialize_q1 (outdir, post_date_right_open_range_params(week_posts, 0.3*total_posts, 0.6*total_posts))
   serialize_q12(outdir, post_date_right_open_range_params(week_posts, 0.3*total_posts, 0.6*total_posts))
   serialize_q18(outdir, post_date_right_open_range_params(week_posts, 0.3*total_posts, 0.6*total_posts), post_lengths, language_codes)
   serialize_q10(outdir, key_params(tag_posts, total_posts/900, total_posts/600), post_date_right_open_range_params(week_posts, 0.3*total_posts, 0.6*total_posts))

   serialize_q4 (outdir, key_params(tagclass_posts, total_posts/20, total_posts/10), key_params(country_sample, total_posts/150, total_posts/50))
   serialize_q5 (outdir, key_params(country_sample, total_posts/200, total_posts/100))
   serialize_q6 (outdir, key_params(tag_posts, total_posts/1300, total_posts/900))
   serialize_q7 (outdir, key_params(tag_posts, total_posts/900, total_posts/600))
   serialize_q8 (outdir, key_params(tag_posts, total_posts/600, total_posts/300))
   serialize_q9 (outdir, key_params(tagclass_posts, 6000, 25000))
   serialize_q13(outdir, key_params(country_sample, total_posts/200, total_posts/100))
   serialize_q15(outdir, key_params(country_sample, total_posts/200, total_posts/100))
   serialize_q16(outdir, persons, key_params(tagclass_posts, total_posts/30, total_posts/10), key_params(country_sample, total_posts/80, total_posts/20), path_bounds)
   serialize_q17(outdir, key_params(country_sample, total_posts/200, total_posts/100))
   serialize_q19(outdir, key_params(tagclass_posts, total_posts/60, total_posts/10))
   serialize_q21(outdir, key_params(country_sample, total_posts/200, total_posts/100))
   serialize_q22(outdir, key_params(country_sample, total_posts/120, total_posts/40))
   serialize_q23(outdir, key_params(country_sample, total_posts/200, total_posts/100))
   serialize_q24(outdir, key_params(tagclass_posts, total_posts/140, total_posts/5))
   serialize_q25(outdir, persons, post_months)

   # TODO: Refine
   serialize_q20(outdir, key_params(tagclass_posts, total_posts/20, total_posts/2))
   serialize_q11(outdir, key_params(country_sample, total_posts/80, total_posts/20), bad_words)

if __name__ == "__main__":
   sys.exit(main())
