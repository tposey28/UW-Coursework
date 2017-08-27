#include "BabyMonster.h"
#include "Directions.h"
#include <cmath>

BabyMonster::BabyMonster(int row_0, int col_0, Sense* s, Character* prey, int logsize)
	: Monster(row_0, col_0, s, prey, logsize) { }
	bool _move = true;

int BabyMonster::get_move() {
	_move = !_move;
	if (_move) {
		return Monster::get_move();
	} else {
		return Directions::NONE;
	}
}

char BabyMonster::symbol() const {
	return 'm';
}
