#ifndef BABY_MONSTER_H_
#define BABY_MONSTER_H_
#include "Character.h"
#include "Monster.h"
#include "Sense.h"

class BabyMonster : public Monster {
	public:
		BabyMonster(int row_0, int col_0, Sense* s, Character* prey, int logsize = 10);
	int get_move();
	char symbol() const;

	private:
		bool _move;
};
#endif
