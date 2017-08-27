#include "Property.h"

BEGIN_ESTATE_NAMESPACE

// Initializing the static data member
int Property::s_count = 0;


int Property::getCount() {
  return s_count;
}


Property::Property(int price)
  : Asset(price)
  , _id(s_count++) 
{
  PRINT(toString() + " constructed\n");
}

Property::Property(const Property& old) 
  : Asset(old._price)
  , _id(s_count++) 
{
  PRINT(toString() + " copy_constructed\n");
}

Property::~Property() {
  PRINT(toString() + " destructed\n");
}

string Property::toString() {
    
  stringstream s;

  s << "Property={"
    << "id=" << _id
    << ", price=" << this->_price // this unnecessary here
    << "}";
  
  return s.str();
      
}


END_ESTATE_NAMESPACE
