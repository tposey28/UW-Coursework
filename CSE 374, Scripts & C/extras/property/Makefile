CXX = g++
CXXFLAGS = -Wall -g -std=c++11
LDFLAGS = 

# for lots more output
CXXFLAGS += -DDEBUG 

PROGRAMS = estate

OBJ  = Property.o House.o Land.o main.o Asset.o
HEADERS = Property.h House.h Land.h Asset.h common.h 

all: $(PROGRAMS)

%.o: %.cc %.h common.h
	$(CXX) $(CXXFLAGS) -c $<

main.o: main.cc $(HEADERS)
	$(CXX) $(CXXFLAGS) -c $<

estate: $(OBJ)
	$(CXX) $(LDFLAGS) -o $@ $^

clean:
	rm -f *.o $(PROGRAMS)
