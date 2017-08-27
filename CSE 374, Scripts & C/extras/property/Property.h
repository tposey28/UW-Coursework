#ifndef PROPERTY_H
#define PROPERTY_H

#include <iostream>
#include <sstream>
#include "Asset.h"

BEGIN_ESTATE_NAMESPACE

/**
 * Represents a property for sale.
 * Beyond the price methods of Asset, 
 * it adds a unique id to every instance
 */
class Property : public Asset {

 public:

  static int getCount();

  Property(int price = 0); // default arg (implicit constructor)
  Property(const Property& old);

  virtual ~Property();

  string toString();

 protected: // Note: we have changed access mode from public to protected
  int  _id;

 private:   // Will be inaccessible in derived classes 
  static int s_count;

};

END_ESTATE_NAMESPACE

#endif
