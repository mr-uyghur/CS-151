
/**
 * SeatClass.java
 * ---------------------------------------------------------
 * Enum defining three seat classes and their corresponding prices.
 */

enum SeatClass { FIRST, ECON_PLUS, ECONOMY;
  static int priceOf(SeatClass c){
    switch(c){
      case FIRST: return 1000;
      case ECON_PLUS: return 500;
      default: return 250;
    }
  }
}
