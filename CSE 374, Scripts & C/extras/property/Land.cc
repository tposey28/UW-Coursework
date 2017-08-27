#include "Land.h"

BEGIN_ESTATE_NAMESPACE

// Examine the constructor carefully
// If we did not invoke the constructor explicitly,
// the Property() constructor would have been called automatically
// for us. You should try it.
Land::Land(int price, int lot_size, bool water) 
   : Property(price),
    _lot_size(lot_size),
    _waterfront(water)
{
  PRINT(toString() + " constructed\n");
}

Land::Land(const Land& old)
  : Property(old),
    _lot_size(old._lot_size),
    _waterfront(old._waterfront)
{
  PRINT(toString() + " copy_constructed\n");
}


Land::~Land() {

  PRINT(toString()+" destructed\n");
  
}

string Land::toString() {

  stringstream s;

  s << "Land={"
    << Property::toString() // a super call
    << ", lot_size=" << _lot_size
    << ", water=" << _waterfront
    << "}";
  
  return s.str();

}

END_ESTATE_NAMESPACE
