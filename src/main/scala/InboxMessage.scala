import InboxMessage.ones

/**
 * @author Ratskevich Natalia reimai@yandex-team.ru
 */
case class InboxMessage(bytes: Array[Byte] = ones.clone())

object InboxMessage {
  private val ones = Array.fill[Byte](256 * 1024)(1)
}
