#ifndef ASSET_H
#define ASSET_H
#include <string>
#include "common.h"
BEGIN_ESTATE_NAMESPACE

class Asset {
  public:
    virtual ~Asset();
    void adjustPrice(int new_price);
    int getPrice(); 

    // toString is _pure_ virtual, so its implementation must
    // be provided by a derived class.
    // No instance of Asset can be created directly because it is missing toString() implementation
    // (like abstract class/method in Java)
    virtual string toString() = 0;
 
 protected: // Note: we have changed access mode from public to protected
  
  // Even though Asset cannot be instantiated alone, a constructor
  // is still useful for initializing the price field. We will
  // make it protected to discourage users from creating Assets.
  Asset(int price = 0); // default arg (implicit constructor)

  int  _price;
};

END_ESTATE_NAMESPACE
#endif
