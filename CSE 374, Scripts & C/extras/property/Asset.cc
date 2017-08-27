#include "Asset.h"

BEGIN_ESTATE_NAMESPACE

Asset::Asset(int price) 
  : _price(price)
{
}

void Asset::adjustPrice(int new_price) {
  _price = new_price;
}


int Asset::getPrice() {
  return _price;
}


Asset::~Asset() { }

END_ESTATE_NAMESPACE
