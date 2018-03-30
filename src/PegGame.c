/*
 * Program:		Peg Board Crusher
 * Author:		Bryan A. Bueter
 * Date:		03-23-2001
 *
 * Description: This program systematically determines all solutions to that
 * annoying little peg board game, distributed by Cracker Barrell(TM) Restaurants.
 * All command line switches are for output format only.  This basically does the
 * same thing everytime you run it.
 *
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "PegGame.h"

void PlayRound (PegBoard *CurrentBoard, Moves *CurrentMoves, int CurrentMove, int Peg, int Direction) {
/*
 * This function proccesses the decisions of a peg board round.  From whatever peg
 * is passed as the first, to Peg15, it processes each direction until it finds a
 * good move.  When a good move is found, it checks for another good move.  If there
 * is another good move, it will call itself with a copy of the current board.
 *
 * This function does not attempt to make a move in the direction it is called in.
 * This is so no move is taken twice.
 */
	PegBoard *NewBoard;
	Moves *NewMoves;

	if (Direction > Left) {
	/*
	 * Increment pointers.  We want the next direction, or the next peg.
	 */
		if (Peg >= Peg15) {
			Peg = Peg1;
			Direction = TopLeft;
		} else {
			Peg++;
			Direction = TopLeft;
		}
	} else
		Direction++;

	while (OneMove(CurrentBoard, Peg1, TopLeft) == EXIT_SUCCESS) {
	/*
	 * While there is at least one good move on the board, play the game.
	 */
		if (ValidMove(CurrentBoard, Peg, Direction) == EXIT_SUCCESS) {
			/*
			 * The current Position yeilds a valid move, Play new Round and move.
			 */
			if (OneMove(CurrentBoard, Peg, Direction+1) == EXIT_SUCCESS) {
				/* 
				 * Make a new Board and Moves list,
				 * copy the pegs and moves,
				 * and Reset the direction Pointers
				 */
				NewBoard = malloc(PegBoard_Mem_Size);
				NewMoves = malloc(Moves_Mem_Size);
				memcpy(NewBoard, CurrentBoard, PegBoard_Mem_Size);
				memcpy(NewMoves, CurrentMoves, Moves_Mem_Size);
				ResetBoard(NewBoard, -1);

				/* Begin a new round */
				PlayRound(NewBoard, NewMoves, CurrentMove, Peg, Direction);

				/* Save some memory */
				free(NewBoard);
				free(NewMoves);
			}/* End if() */

			MakeMove(CurrentBoard, CurrentMoves, &CurrentMove, Peg, Direction);
			Peg = Peg1;
			Direction = TopLeft;
		} else {
			if (Direction >= Left) {
			/*
			 * Increment pointers.
			 */
				if (Peg >= Peg15) {
					Peg = Peg1;
					Direction = TopLeft;
				} else {
					Peg++;
					Direction = TopLeft;
				}/* End if() */

			} else {
				Direction++;
			}/* End if() */

		}/* End if() */

	}/* End while() */


	PrintResults(CurrentMoves, CurrentMove);

}

void Usage(void) {
/*
 * Simple function that prints the usage of this program.
 */
	printf("Usage: peggame [-s | -d] [-t] [-r <peg>]\n\n");
	printf("This program accepts the arguments -s, -d, -t, or -r\n");
	printf("  -t    Displays total number of solutions in each round.\n");
	printf("  -s    Displays every solution.  This is very verbose.\n");
	printf("        You may want to consider ding a \'-t\' by itself first\n");
	printf("  -d	Dump output without prompting.  Must be accompanied by \'-s\'\n");
	printf("  -r n  Play one round, with peg \'n\' empty.  Where 1 <= n <= 15.\n");
}

int main(int argc, char *argv[]) {

	PegBoard Board[15];		/* Peg Board Data Type */
	Moves Solutions[15];	/* Moves up till now */
	int BlankPeg = -1;		/* From the command line, run one round with this peg empty */
	int i,j,k;				/* Counters */
	char *arg_ptr;			/* Pointer to parse argc[] */

	if (argc < 2) {
	/*
	 * Exit with FAILURE on no arguments.
	 */
		Usage();
		exit(EXIT_FAILURE);
	}

	for (j=0; j<argc; j++) {
	/*
	 * Begin parsing command line arguments.
	 */
		arg_ptr = argv[j];
		if (*arg_ptr == '-') {
			arg_ptr++;
			switch (*arg_ptr) {
			case 's':
			/*
			 * Set flag to print Solutions
			 */
				Display_Solutions = 1;
				break;
			case 'd':
			/*
			 * Set flag to dump Solutions
			 */
				if (Display_Solutions != 1) {
					Usage();
					exit(EXIT_FAILURE);
				} else
					Display_Dump = 1;
				break;
			case 't':
			/*
			 * Set flag to print Total Solutions per Round
			 */
				Display_Total_Solutions = 1;
				break;
			case 'r':
			/*
			 * Play just one round with empty peg from command line.  Exit on error.
			 */
				if (j < argc-1) {
					j++;
					arg_ptr = argv[j];

					k=0;
					while (arg_ptr[k] >= '0' && arg_ptr[k] <= '9' ) {
						k++;
					}
					arg_ptr[k] = 0;

					BlankPeg = atoi(arg_ptr);
					BlankPeg--;		/* Make Blank Peg an index value */
					if (BlankPeg < Peg1 || BlankPeg > Peg15) {
					/*
					 * Error, Peg number not within range.
					 */
						Usage();
						exit(EXIT_FAILURE);
					}
				} else {
				/*
				 * Error, no peg number supplied.
				 */
					Usage();
					exit(EXIT_FAILURE);
				}
				break;
			case 'h':
				Usage();
				exit(EXIT_SUCCESS);
			default:
				Usage();
				exit(EXIT_FAILURE);

			}/* End of switch() */

		}/* End of if() */

	}/* End of for() */

	if (BlankPeg > 0) {
	/*
	 * Only play round with Specified BlankPeg
	 */
		ResetBoard(Board, BlankPeg);
		printf("Playing one round with Peg %d blank\n",BlankPeg+1);
		PlayRound(Board,Solutions,0,Peg1,TopLeft);
		if (Display_Total_Solutions == 1)
			printf("%d Total solutions for Peg %d\n",Total_Solutions,BlankPeg+1);
		exit(EXIT_SUCCESS);
	}

	for (i=Peg1; i<Peg6; i++) {
	/*
	 * For each Peg 1->6, play round with that peg empty.
	 */
		ResetBoard(Board, i);
		printf("Playing with Peg %d blank\n",i+1);
		PlayRound(Board,Solutions,0,Peg1,TopLeft);
		if (Display_Total_Solutions == 1)
			printf("%d Total solutions for Peg %d\n",Total_Solutions,i+1);
		Total_Solutions = 0;
	}

	exit(EXIT_SUCCESS);

}/* End of main() */
