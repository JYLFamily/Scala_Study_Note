package Basic

class myAccount extends Account with logger {
  override def save: Unit = {
    log("100")
  }
}
