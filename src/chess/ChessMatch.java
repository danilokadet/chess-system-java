package chess;

import java.util.ArrayList;
import java.util.List;

import boradgame.Board;
import boradgame.Piece;
import boradgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	private int turn;
	private Color currentPlayer;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturePieces = new ArrayList<>();

	public ChessMatch() {

		board = new Board(8, 8);
		this.initialSetup();
		this.turn = 1;
		this.currentPlayer = Color.WHITE;

	}

	public int getTurn() {

		return this.turn;
	}

	public Color getCurrentPlayer() {

		return this.currentPlayer;
	}

	public boolean[][] possibleMoves(ChassPosition sourcePosition) {

		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}

	public ChessPiece[][] getPieces() {

		ChessPiece[][] mat = new ChessPiece[this.board.getRows()][this.board.getColumns()];

		for (int i = 0; i < this.board.getRows(); i++) {

			for (int j = 0; j < this.board.getColumns(); j++) {

				mat[i][j] = (ChessPiece) this.board.piece(i, j);
			}
		}

		return mat;
	}

	public ChessPiece performChessMove(ChassPosition sourcePosition, ChassPosition targetPosition) {

		Position source = sourcePosition.toPosition();
		Position target = sourcePosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturePiece = makeMove(source, target);
		this.nextTurn();
		return (ChessPiece) capturePiece;

	}

	private void validateTargetPosition(Position source, Position target) {

		if (!board.piece(source).possibleMove(target)) {

			throw new ChessException("The chosen piece can't move to target position");
		}
	}

	private Piece makeMove(Position source, Position target) {

		Piece p = board.removePiece(source);
		Piece capturePiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if (capturePiece != null) {
			
			piecesOnTheBoard.remove(capturePiece);
			capturePieces.add(capturePiece);
		}
		return capturePiece;
	}

	private void validateSourcePosition(Position position) {

		if (!board.thereIsAPiece(position)) {

			throw new ChessException("There is no piece on source position");
		}

		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {

			throw new ChessException("The chosen piece is not yours");
		}
		
		if (!board.piece(position).isThereAnyPossibleMove()) {

			throw new ChessException("There is no possible moves for the chosen pie");
		}
	}

	private void nextTurn() {

		this.turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {

		board.placePiece(piece, new ChassPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	private void initialSetup() {

		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
		placeNewPiece('c', 2, new Rook(board, Color.WHITE));
		placeNewPiece('d', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 1, new Rook(board, Color.WHITE));
		placeNewPiece('d', 1, new King(board, Color.WHITE));

		placeNewPiece('c', 7, new Rook(board, Color.BLACK));
		placeNewPiece('c', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
