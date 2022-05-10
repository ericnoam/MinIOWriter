package com.lv

import ru.tinkoff.phobos.decoding.{ElementDecoder, XmlDecoder}
import ru.tinkoff.phobos.derivation.semiauto.{deriveElementDecoder, deriveElementEncoder, deriveXmlDecoder, deriveXmlEncoder}
import ru.tinkoff.phobos.encoding.{ElementEncoder, XmlEncoder}

object models {
  type PlayerID = String
  type GameID = String
  type GameSessionID  = String
  type ProcessingCode = String
  type TransactionType = String

  case class Transaction(processingCode: ProcessingCode, transactionType: TransactionType, gameId: GameID, gameSessionId: GameSessionID, trxDatetime: String, totalAmount: Double)
  case class Player(playerID: PlayerID, balanceEod: Double, Transaction: List[Transaction])
  case class AuditFile(Player: Player)

  object Transaction {
    implicit val transactionElementEncoder: ElementEncoder[Transaction] = deriveElementEncoder
    implicit val transactionElementDecoder: ElementDecoder[Transaction] = deriveElementDecoder
  }

  object Player {
    implicit val playerElementEncoder: ElementEncoder[Player] = deriveElementEncoder
    implicit val playerElementDecoder: ElementDecoder[Player] = deriveElementDecoder
  }

  object AuditFile {
    implicit val auditFileXmlEncoder: XmlEncoder[AuditFile] = deriveXmlEncoder("AuditFile")
    implicit val auditFileXmlDecoder: XmlDecoder[AuditFile] = deriveXmlDecoder("AuditFile")
  }
}
