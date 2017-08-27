#include <iostream>
#include "Asset.h"
#include "Property.h"
#include "House.h"
#include "Land.h"

using namespace std;
using namespace ESTATE;

void test_new_delete() {

  // Dynamically allocating and freeing an integer
  int *p_int = new int;
  *p_int = 3;
  cout << "*p_int " << *p_int << endl;
  delete p_int;

  // Same as above but initializing directly with new
  int *p_int2 = new int(3);
  cout << "*p_int2 " << *p_int2 << endl;
  delete p_int2;

  // And now with an array
  int *p_array = new int[10];
  for ( int i = 0; i < 10; i++ ) {
    p_array[i] = i;
  }
  cout << "p_array[3] " << p_array[3] << endl;
  delete [] p_array; // space leak w/o the brackets (unlike free)


}

void by_value(Property p) {

  cout << "Inside by value: " << p.toString() << endl;

}

void by_reference(Property& p) {

  // Note that we can modify p and the changes will be reflected
  // to the caller
  p.adjustPrice(100);
  cout << "Inside by reference: " << p.toString() << endl;

}


int main() {

  int price = 300000;

  cout << "------------- PART 1 ------------\n";

  // To access static data member or function, no need to
  // have an instance of the class, can use class name directly
  cout << "Accessing static member: " << Property::getCount() << endl;

  // First, we create an object on the stack
  Property p1(price);
  p1.adjustPrice(200000);
  cout << p1.toString() << endl;

  // Let's create another object on the stack using a different constructor
  Property q(p1);
  cout << q.toString() << endl;

  // calls the constructor that takes no arguments
  // if we did not have one, would not compile
  Property x; 
  cout << x.toString() << endl;

  cout << "------------- PART 2 ------------\n";

  // Second, we create an object on the heap
  // In C++, memory management is done with new and delete
  Property *p2 = new Property(price);
  p2->adjustPrice(150000);  
  cout << p2->toString() << endl;
  delete p2;

  // A few more example of using new and delete
  test_new_delete();

  // We can also create an array of 10 Property objects on the heap
  cout << "Array test" << endl;
  Property *prop = new Property[10];
  for ( int i = 0; i < 10; ++i) {
    prop[i].adjustPrice(2*price);
  }
  for ( int i = 0; i < 10; i++ ) {
    cout << "Iterating over array: " << prop[i].toString() << endl;
  }
  delete [] prop;

  cout << "------------- PART 3 ------------\n";
  cout << "Testing copy constructor" << endl;

  // The "=" operator invokes the copy constructor
  // By default, the copy constructor copies all data members
  // This gets tricky when a class contains pointers
  // You have to decide if you want the copy constructor
  // to perform a shallow copy or a deep copy of your object
  Property p3 = p1;

  cout << p3.toString() << endl;

  cout << "------------- PART 4 ------------\n";
  cout << "Passing arguments by value or by reference" << endl;

  // Passing an object by value makes a copy of the object
  by_value(p3);

  // Passing an object by reference does not make a copy
  // Note that in C++, you can pass other data types by reference as well
  // such as integers, floats, etc.
  by_reference(p3);
  cout << "After call by reference: " << p3.toString() << endl;

  // Passing a pointer would also work of course, like in C

  cout << "------------- PART 5 ------------\n";
  cout << "Examples of subclassing" << endl;

  const int land_price = 300000;
  const int land_size  = 10000;

  const int house_price = 600000;
  const int house_size =  2000;

  const int property_price = 100000;

  const int price_increment = 500;
  const int size_increment = 50;

  Land     l1(land_price,land_size);

  PRINT("-----------\n");
  House    h1(house_price,house_size,land_size);
  PRINT("-----------PART 6------------\n");

  // Polymorphic access to Assets
  const int num_assets = 3;
  Asset* assets[num_assets];
  // add a variety of Assets to the array
  for (int i=0; i<num_assets; i++) {
    switch (i%3) {
      case 0:
        assets[i] = new Land(land_price+i*price_increment, land_size+i*size_increment);
        break;
      case 1:
        assets[i] = new House(house_price+i*price_increment, house_size+i*size_increment, land_size+i*size_increment);
      case 2:
        assets[i] = new Property(property_price);
        break;
    }
  }
  PRINT("-----------\n");

  // print out all Assets
  for (int i=0; i<num_assets; i++) {
    cout << "asset[" << i << "]= " << assets[i]->toString() << endl;
  }
  PRINT("-----------\n");
  
  // destruct all Assets in assets
  for (int i=0; i<num_assets; i++) {
    delete assets[i];
  }
  PRINT("-----------\n");

  return 0;

}
