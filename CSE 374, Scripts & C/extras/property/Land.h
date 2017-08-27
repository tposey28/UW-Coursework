#ifndef LAND_H
#define LAND_H

#include "common.h"
#include "Property.h"

BEGIN_ESTATE_NAMESPACE


class Land : public Property {

 public:

  Land(int price, int lot_size, bool water=false);
  Land(const Land& old);

  ~Land();

  string toString();


 private:
  int _lot_size;
  bool _waterfront;
  

};

END_ESTATE_NAMESPACE

#endif
