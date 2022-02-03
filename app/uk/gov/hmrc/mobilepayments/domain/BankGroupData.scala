/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.mobilepayments.domain

import play.api.libs.json.{Json, OFormat}

final case class BankGroupData(
  bankGroupName:          String,
  bankGroupNameFormatted: String,
  banksInGroup:           List[Bank],
  iconUrl:                String)

object BankGroupData {
  implicit val format: OFormat[BankGroupData] = Json.format[BankGroupData]

  def buildBankGroupData(banks: List[Bank]): BankGroupData =
    BankGroupData(
      bankGroupName          = banks.headOption.fold("Unknown Bank")(_.group),
      bankGroupNameFormatted = banks.headOption.fold("Unknown Bank")(_.group).replaceAll("[ &!@£$*()^*]", "-"),
      banksInGroup           = sortBanksInGroup(banks),
      iconUrl                = banks.headOption.fold("Unknown Bank Logo Url")(_.iconUrl)
    )

  def sortBanksInGroup(banksInGroup: List[Bank]): List[Bank] = {
    val maybeBankToPutTop = banksInGroup.find(nextBank => banksInGroupThatNeedToBeDisplayedFirst.contains(nextBank.name)
    )
    val maybeBankToPutBottom =
      banksInGroup.find(nextBank => banksInGroupThatNeedToBeDisplayedLast.contains(nextBank.name))

    (maybeBankToPutTop, maybeBankToPutBottom) match {
      case (Some(bankToPutTop), Some(bankToPutBottom)) =>
        (bankToPutTop :: alphabetize(removeExceptionalBanks(banksInGroup, bankToPutTop, bankToPutBottom))) :+ bankToPutBottom
      case (Some(bankToPutTop), None) => bankToPutTop :: alphabetize(removeExceptionalBank(banksInGroup, bankToPutTop))
      case (None, Some(bankToPutBottom)) =>
        alphabetize(removeExceptionalBank(banksInGroup, bankToPutBottom)) :+ bankToPutBottom
      case _ => alphabetize(banksInGroup)
    }
  }

  private def removeExceptionalBank(
    banksInGroup: List[Bank],
    bankToRemove: Bank
  ): List[Bank] =
    banksInGroup.filterNot(_.name == bankToRemove.name)

  private def removeExceptionalBanks(
    banksInGroup:    List[Bank],
    bankToPutTop:    Bank,
    bankToPutBottom: Bank
  ): List[Bank] =
    banksInGroup.filterNot(nextBankDescription =>
      nextBankDescription.name == bankToPutTop.name || nextBankDescription.name == bankToPutBottom.name
    )

  private def alphabetize(banksInGroup: List[Bank]): List[Bank] =
    banksInGroup.sortWith(_.name < _.name)

  private val banksInGroupThatNeedToBeDisplayedFirst: List[String] = List(
    "Barclays",
    "HSBC",
    "Lloyds Personal",
    "NatWest Online and Mobile Banking",
    "RBS Online and Mobile Banking",
    "AIB",
    "Bank Of Ireland 365 Online UK",
    "Bank Of Scotland Personal",
    "Ulster Bank NI Anytime Internet Banking"
  )

  private val banksInGroupThatNeedToBeDisplayedLast: List[String] = List(
    "RBS Bankline",
    "Ulster Bank NI Bankline",
    "NatWest Bankline"
  )
}
