/*
 * This include file contains five functions MakeMove, OneMove, PrintResults,
 * ResetBoard, and ValidMove.  These functions define all operations and conclusions
 * that can be applied to a Peg Board.
 *
 * This also defines the PegBoard and Moves structure and the memory allocation sizes
 * for a standard declaration.
 */

/*
 * An object to represent a Peg On the Board.
 */
typedef struct pegboard{
	struct pegboard* Direction[6];	/* Pointers to a Peg in all six directions */
	int Value;						/* Value of Peg.  0 = empty, 1 = occupied */
} PegBoard;

/*
 * Find the size of an entire peg board
 */
const PegBoard TestBoard[15];
const int PegBoard_Mem_Size = sizeof(TestBoard);

/*
 * An object to represent a move.
 */
typedef struct moves{
	int FromPeg;	/* Integer representation of the From Peg number */
	int ToPeg;		/* Integer representation of the To Peg number */
} Moves;

/*
 * Find the size of an entire Moves list
 */
const Moves TestMoves[15];
const int Moves_Mem_Size = sizeof(TestMoves);

/*
 * Some output flags
 */
int Display_Solutions = 0;
int Display_Dump = 0;
int Display_Total_Solutions = 0;
int Total_Solutions = 0;

/*
 * Define some constants that will make this program easier to follow.
 */
#define TopLeft		0
#define TopRight	1
#define Right		2
#define BotRight	3
#define BotLeft		4
#define Left		5

#define Peg1	0
#define Peg2	1
#define Peg3	2
#define Peg4	3
#define Peg5	4
#define Peg6	5
#define Peg7	6
#define Peg8	7
#define Peg9	8
#define Peg10	9
#define Peg11	10
#define Peg12	11
#define Peg13	12
#define Peg14	13
#define Peg15	14


int ValidMove(PegBoard *CurrentBoard, int Peg, int Direction) {
/*
 * This function determines if the current Peg and Direction yield a valid move.
 */
	if (Peg < Peg1 || Peg > Peg15)
		return(EXIT_FAILURE);
	if (Direction < TopLeft || Direction > Left)
		return(EXIT_FAILURE);

	if (CurrentBoard[Peg].Value == 0) {
		/*
		 * Current Peg is empty.
		 */
		return(EXIT_FAILURE);
	} else if (CurrentBoard[Peg].Direction[Direction] == NULL) {
		/*
		 * The Current Peg cannot move in the Current Direction.
		 */
		return(EXIT_FAILURE);
	} else if ( CurrentBoard[Peg].Direction[Direction]->Value == 0) {
		/*
		 * Adjacent Peg is empty.
		 */
		return(EXIT_FAILURE);
	} else if ( CurrentBoard[Peg].Direction[Direction]->Direction[Direction] == NULL ) {
		/*
		 * Can't jump Peg.
		 */
		return(EXIT_FAILURE);
	} else if ( CurrentBoard[Peg].Direction[Direction]->Direction[Direction]->Value == 1 ) {
		/*
		 * Can't jump Peg.
		 */
		return(EXIT_FAILURE);
	}

	return(EXIT_SUCCESS);
} /* End of ValidMove() */

int OneMove (PegBoard *CurrentBoard, int Peg, int Direction) {
/*
 * This function looks for at least one good move.
 */
	while (ValidMove(CurrentBoard, Peg, Direction) == EXIT_FAILURE) {
		if (Direction >= Left) {
			if (Peg >= Peg15)
				return(EXIT_FAILURE);
			Peg++;
			Direction = TopLeft;
		} else
			Direction++;
	}
	return(EXIT_SUCCESS);

}/* End OneMove() */

void MakeMove (PegBoard *CurrentBoard, Moves *CurrentMoves, int *CurrentMove, int Peg, int Direction) {
/*
 * This function Moves the current Peg in the current Direction and updates the
 * move list and the PegBoard.
 */
	CurrentBoard[Peg].Direction[Direction]->Direction[Direction]->Value = 1;
	CurrentBoard[Peg].Direction[Direction]->Value = 0;
	CurrentBoard[Peg].Value = 0;
	
	/*
	 * Update our Moves list.  Gosh I wish there was a better way to do this...
	 */
	CurrentMoves[*CurrentMove].FromPeg = Peg;
	switch (Peg) {
	case Peg1:
		switch (Direction) {
		case BotLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg4;
			break;
		case BotRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg6;
			break;
		}
		break;
	case Peg2:
		switch (Direction) {
		case BotLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg7;
			break;
		case BotRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg9;
			break;
		}
		break;
	case Peg3:
		switch (Direction) {
		case BotLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg8;
			break;
		case BotRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg10;
			break;
		}
		break;
	case Peg4:
		switch (Direction) {
		case TopRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg1;
			break;
		case Right:
			CurrentMoves[*CurrentMove].ToPeg = Peg6;
			break;
		case BotRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg13;
			break;
		case BotLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg11;
			break;
		}
		break;
	case Peg5:
		switch (Direction) {
		case BotRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg14;
			break;
		case BotLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg12;
			break;
		}
		break;
	case Peg6:
		switch (Direction) {
		case TopLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg1;
			break;
		case BotRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg15;
			break;
		case BotLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg13;
			break;
		case Left:
			CurrentMoves[*CurrentMove].ToPeg = Peg4;
			break;
		}
		break;
	case Peg7:
		switch (Direction) {
		case TopRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg2;
			break;
		case Right:
			CurrentMoves[*CurrentMove].ToPeg = Peg9;
			break;
		}
		break;
	case Peg8:
		switch (Direction) {
		case TopRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg3;
			break;
		case Right:
			CurrentMoves[*CurrentMove].ToPeg = Peg10;
			break;
		}
		break;
	case Peg9:
		switch (Direction) {
		case TopLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg2;
			break;
		case Left:
			CurrentMoves[*CurrentMove].ToPeg = Peg7;
			break;
		}
		break;
	case Peg10:
		switch (Direction) {
		case TopLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg3;
			break;
		case Left:
			CurrentMoves[*CurrentMove].ToPeg = Peg8;
			break;
		}
		break;
	case Peg11:
		switch (Direction) {
		case TopRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg4;
			break;
		case Right:
			CurrentMoves[*CurrentMove].ToPeg = Peg13;
			break;
		}
		break;
	case Peg12:
		switch (Direction) {
		case TopRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg5;
			break;
		case Right:
			CurrentMoves[*CurrentMove].ToPeg = Peg14;
			break;
		}
		break;
	case Peg13:
		switch (Direction) {
		case TopLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg4;
			break;
		case TopRight:
			CurrentMoves[*CurrentMove].ToPeg = Peg6;
			break;
		case Right:
			CurrentMoves[*CurrentMove].ToPeg = Peg15;
			break;
		case Left:
			CurrentMoves[*CurrentMove].ToPeg = Peg11;
			break;
		}
		break;
	case Peg14:
		switch (Direction) {
		case TopLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg5;
			break;
		case Left:
			CurrentMoves[*CurrentMove].ToPeg = Peg12;
			break;
		}
		break;
	case Peg15:
		switch (Direction) {
		case TopLeft:
			CurrentMoves[*CurrentMove].ToPeg = Peg6;
			break;
		case Left:
			CurrentMoves[*CurrentMove].ToPeg = Peg13;
			break;
		}
		break;
	} /* End of switch(Peg) */
	++*CurrentMove;

} /* End of MakeMove() */

void PrintResults (Moves *CurrentMoves, int TotalMoves) {
/*
 * This function prints the results if a solution was found.
 */
  int i;
  char dummy;

  if (TotalMoves > 12) {
	if (Display_Solutions == 1) {
		for (i=0; i<TotalMoves; i++) {
                        printf("%d:%d ", CurrentMoves[i].FromPeg+1, CurrentMoves[i].ToPeg+1);
		}
		printf("\n");

		if (Display_Dump == 0) {
			printf("\nPress the Enter key to continue:");
			scanf("%c", &dummy);
		}

	}/* End of if() */

	Total_Solutions++;

  }/* End of if() */

}/* End of PrintResults() */

void ResetBoard (PegBoard *Board, int EmptyPeg) {
/*
 * Set direction pointers for pegs to adjacent pegs.  All border directions
 * are NULL.
 *
 * This function also allows for updating the pointers without changing the
 * values of the pegs by passing a '-1' for the EmptyPeg.  This is so we can
 * copy the board without pointing back to the original board.
 */
  int i;

  Board[Peg1].Direction[TopLeft]	= NULL;
  Board[Peg1].Direction[TopRight]	= NULL;
  Board[Peg1].Direction[Right]		= NULL;
  Board[Peg1].Direction[BotRight]	= &Board[Peg3];
  Board[Peg1].Direction[BotLeft]	= &Board[Peg2];
  Board[Peg1].Direction[Left]		= NULL;

  Board[Peg2].Direction[TopLeft]	= NULL;
  Board[Peg2].Direction[TopRight]	= &Board[Peg1];
  Board[Peg2].Direction[Right]		= &Board[Peg3];
  Board[Peg2].Direction[BotRight]	= &Board[Peg5];
  Board[Peg2].Direction[BotLeft]	= &Board[Peg4];
  Board[Peg2].Direction[Left]		= NULL;

  Board[Peg3].Direction[TopLeft]	= &Board[Peg1];
  Board[Peg3].Direction[TopRight]	= NULL;
  Board[Peg3].Direction[Right]		= NULL;
  Board[Peg3].Direction[BotRight]	= &Board[Peg6];
  Board[Peg3].Direction[BotLeft]	= &Board[Peg5];
  Board[Peg3].Direction[Left]		= &Board[Peg2];

  Board[Peg4].Direction[TopLeft]	= NULL;
  Board[Peg4].Direction[TopRight]	= &Board[Peg2];
  Board[Peg4].Direction[Right]		= &Board[Peg5];
  Board[Peg4].Direction[BotRight]	= &Board[Peg8];
  Board[Peg4].Direction[BotLeft]	= &Board[Peg7];
  Board[Peg4].Direction[Left]		= NULL;

  Board[Peg5].Direction[TopLeft]	= &Board[Peg2];
  Board[Peg5].Direction[TopRight]	= &Board[Peg3];
  Board[Peg5].Direction[Right]		= &Board[Peg6];
  Board[Peg5].Direction[BotRight]	= &Board[Peg9];
  Board[Peg5].Direction[BotLeft]	= &Board[Peg8];
  Board[Peg5].Direction[Left]		= &Board[Peg4];

  Board[Peg6].Direction[TopLeft]	= &Board[Peg3];
  Board[Peg6].Direction[TopRight]	= NULL;
  Board[Peg6].Direction[Right]		= NULL;
  Board[Peg6].Direction[BotRight]	= &Board[Peg10];
  Board[Peg6].Direction[BotLeft]	= &Board[Peg9];
  Board[Peg6].Direction[Left]		= &Board[Peg5];

  Board[Peg7].Direction[TopLeft]	= NULL;
  Board[Peg7].Direction[TopRight]	= &Board[Peg4];
  Board[Peg7].Direction[Right]		= &Board[Peg8];
  Board[Peg7].Direction[BotRight]	= &Board[Peg12];
  Board[Peg7].Direction[BotLeft]	= &Board[Peg11];
  Board[Peg7].Direction[Left]		= NULL;

  Board[Peg8].Direction[TopLeft]	= &Board[Peg4];
  Board[Peg8].Direction[TopRight]	= &Board[Peg5];
  Board[Peg8].Direction[Right]		= &Board[Peg9];
  Board[Peg8].Direction[BotRight]	= &Board[Peg13];
  Board[Peg8].Direction[BotLeft]	= &Board[Peg12];
  Board[Peg8].Direction[Left]		= &Board[Peg7];

  Board[Peg9].Direction[TopLeft]	= &Board[Peg5];
  Board[Peg9].Direction[TopRight]	= &Board[Peg6];
  Board[Peg9].Direction[Right]		= &Board[Peg10];
  Board[Peg9].Direction[BotRight]	= &Board[Peg14];
  Board[Peg9].Direction[BotLeft]	= &Board[Peg13];
  Board[Peg9].Direction[Left]		= &Board[Peg8];

  Board[Peg10].Direction[TopLeft]	= &Board[Peg6];
  Board[Peg10].Direction[TopRight]	= NULL;
  Board[Peg10].Direction[Right]		= NULL;
  Board[Peg10].Direction[BotRight]	= &Board[Peg15];
  Board[Peg10].Direction[BotLeft]	= &Board[Peg14];
  Board[Peg10].Direction[Left]		= &Board[Peg9];

  Board[Peg11].Direction[TopLeft]	= NULL;
  Board[Peg11].Direction[TopRight]	= &Board[Peg7];
  Board[Peg11].Direction[Right]		= &Board[Peg12];
  Board[Peg11].Direction[BotRight]	= NULL;
  Board[Peg11].Direction[BotLeft]	= NULL;
  Board[Peg11].Direction[Left]		= NULL;

  Board[Peg12].Direction[TopLeft]	= &Board[Peg7];
  Board[Peg12].Direction[TopRight]	= &Board[Peg8];
  Board[Peg12].Direction[Right]		= &Board[Peg13];
  Board[Peg12].Direction[BotRight]	= NULL;
  Board[Peg12].Direction[BotLeft]	= NULL;
  Board[Peg12].Direction[Left]		= &Board[Peg11];

  Board[Peg13].Direction[TopLeft]	= &Board[Peg8];
  Board[Peg13].Direction[TopRight]	= &Board[Peg9];
  Board[Peg13].Direction[Right]		= &Board[Peg14];
  Board[Peg13].Direction[BotRight]	= NULL;
  Board[Peg13].Direction[BotLeft]	= NULL;
  Board[Peg13].Direction[Left]		= &Board[Peg12];

  Board[Peg14].Direction[TopLeft]	= &Board[Peg9];
  Board[Peg14].Direction[TopRight]	= &Board[Peg10];
  Board[Peg14].Direction[Right]		= &Board[Peg15];
  Board[Peg14].Direction[BotRight]	= NULL;
  Board[Peg14].Direction[BotLeft]	= NULL;
  Board[Peg14].Direction[Left]		= &Board[Peg13];

  Board[Peg15].Direction[TopLeft]	= &Board[Peg10];
  Board[Peg15].Direction[TopRight]	= NULL;
  Board[Peg15].Direction[Right]		= NULL;
  Board[Peg15].Direction[BotRight]	= NULL;
  Board[Peg15].Direction[BotLeft]	= NULL;
  Board[Peg15].Direction[Left]		= &Board[Peg14];

  if (EmptyPeg >= 0) {
  /*
   * Reset the board with one Empty Peg.
   */
	  for (i=Peg1; i<=Peg15; i++) {
		  Board[i].Value = 1;
	  }
	  Board[EmptyPeg].Value = 0;
  }

}/* End of ResetBoard() */
