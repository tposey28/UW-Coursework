#ifndef HOUSE_H
#define HOUSE_H

#include "common.h"
#include "Land.h"

BEGIN_ESTATE_NAMESPACE

class House : public Land {

 public:

  House(int price, int house_size, int lot_size, bool water=false);
  House(const House& old);

  ~House();

  string toString();

 private:
  int _house_size;


};

END_ESTATE_NAMESPACE

#endif
