package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boradgame.Board;
import boradgame.Piece;
import boradgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;

	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturePieces = new ArrayList<>();

	public ChessMatch() {

		board = new Board(8, 8);
		this.initialSetup();
		this.turn = 1;
		this.currentPlayer = Color.WHITE;

	}

	public boolean getCheckMate() {

		return this.checkMate;
	}

	public int getTurn() {

		return this.turn;
	}

	public Color getCurrentPlayer() {

		return this.currentPlayer;
	}

	public boolean getCheck() {
		return this.check;
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
		Piece capturedPiece = makeMove(source, target);

		if (testCheck(currentPlayer)) {

			undoMove(source, target, capturedPiece);

			throw new ChessException("you can't put yourself in check");

		}

		check = (testCheck(opponent(currentPlayer))) ? true : false;

		if (testeCheckMate(opponent(currentPlayer))) {

			checkMate = true;

		} else {
			this.nextTurn();
		}
		return (ChessPiece) capturedPiece;

	}

	private void validateTargetPosition(Position source, Position target) {

		if (!board.piece(source).possibleMove(target)) {

			throw new ChessException("The chosen piece can't move to target position");
		}
	}

	private Piece makeMove(Position source, Position target) {

		ChessPiece p = (ChessPiece) board.removePiece(source);
		p.increaseMoveCount();
		Piece capturePiece = board.removePiece(target);
		board.placePiece(p, target);

		if (capturePiece != null) {

			piecesOnTheBoard.remove(capturePiece);
			capturePieces.add(capturePiece);
		}

		// #special move castling kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {

			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}

		// #special move castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {

			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}

		return capturePiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {

		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);

		if (capturedPiece != null) {

			board.placePiece(capturedPiece, target);
			capturePieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);

		}

		// #special move castling kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {

			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}

		// #special move castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {

			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}

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

	private Color opponent(Color color) {

		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {

		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {

			if (p instanceof King) {

				return (ChessPiece) p;
			}
		}

		throw new IllegalStateException("There is no " + color + " king on the board");
	}

	private boolean testCheck(Color color) {

		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream()
				.filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());

		for (Piece p : opponentPieces) {

			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {

				return true;
			}
		}
		return false;
	}

	private boolean testeCheckMate(Color color) {

		if (!testCheck(color)) {

			return false;
		}

		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());

		for (Piece p : list) {

			boolean[][] mat = p.possibleMoves();

			for (int i = 0; i < board.getRows(); i++) {

				for (int j = 0; j < board.getColumns(); j++) {

					if (mat[i][j]) {

						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testcheck = testCheck(color);
						undoMove(source, target, capturedPiece);

						if (!testcheck) {

							return false;
						}
					}
				}

			}

		}
		return true;
	}

	private void initialSetup() {

		placeNewPiece('c', 2, new Rook(board, Color.WHITE));
		placeNewPiece('d', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 1, new Rook(board, Color.WHITE));
		placeNewPiece('d', 1, new King(board, Color.WHITE, this));

		placeNewPiece('c', 7, new Rook(board, Color.BLACK));
		placeNewPiece('c', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 8, new King(board, Color.BLACK, this));

	}
}
