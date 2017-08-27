#include "House.h"

BEGIN_ESTATE_NAMESPACE

// Examine the constructor carefully
House::House(int price, int house_size, int lot_size, bool water) 
  : Land(price,lot_size,water),
    _house_size(house_size)
{
  PRINT(toString() + " constructed\n");
}

House::House(const House& old)
  : Land(old),
    _house_size(old._house_size)
{
  PRINT(toString() + " copy_constructed\n");
}


House::~House() {

  PRINT(toString()+" destructed\n");

}

string House::toString() {

  stringstream s;

  s << "House={"
    << Land::toString()
    << ", size=" << _house_size
    << "}";
  
  // Note that we could also invoke the
  // toString() method from Property by using
  // Property::toString()

  return s.str();

}

END_ESTATE_NAMESPACE
