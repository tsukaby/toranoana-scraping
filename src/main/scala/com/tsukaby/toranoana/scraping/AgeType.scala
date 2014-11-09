package com.tsukaby.toranoana.scraping

/**
 * 年齢タイプ
 */
sealed trait AgeType {

}

object AgeType {

  object AllAge extends AgeType {

  }

  object AdultOnly extends AgeType {

  }

}