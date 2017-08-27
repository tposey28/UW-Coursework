#ifndef COMMON_H
#define COMMON_H

#define BEGIN_ESTATE_NAMESPACE namespace ESTATE { using namespace std;
#define END_ESTATE_NAMESPACE   }

#ifdef DEBUG
#define PRINT(message) cout << message;
#else
#define PRINT(message)
#endif

#endif
