package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import boradgame.Board;
import boradgame.Position;
import chess.ChassPosition;
import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;

public class Program {

	public static void main(String[] args) {

		ChessMatch chessMatch = new ChessMatch();
		Scanner sc = new Scanner(System.in);
		List<ChessPiece> captured = new ArrayList<>();
		
		while (!chessMatch.getCheckMate()) {
			try {
				UI.clearScreem();
				UI.printMatch(chessMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				ChassPosition source = UI.readChessPosition(sc);
				
				boolean[][] possibleMoves = chessMatch.possibleMoves(source);
				UI.clearScreem();
				UI.printBoard(chessMatch.getPieces(), possibleMoves);
				System.out.println();
				
				System.out.print("Target: ");
				ChassPosition target = UI.readChessPosition(sc);
				
				ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
				
				if (capturedPiece != null) {
					
					captured.add(capturedPiece);
				}
			}catch(ChessException e) {
				
				System.out.print(e.getMessage());
				sc.nextLine();
			}catch(InputMismatchException e) {
				
				System.out.print(e.getMessage());
				sc.nextLine();
			}
		}

		UI.clearScreem();
		UI.printMatch(chessMatch, captured);
	}

}
