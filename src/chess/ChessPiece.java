package chess;

import boradgame.Board;
import boradgame.Piece;
import boradgame.Position;

public abstract class ChessPiece extends Piece {
	
	private Color color;

	public ChessPiece(Board board, Color color) {
		
		super(board);
		this.color = color;
	}
	
	public Color getColor() {
		
		return this.color;
	}

	public ChassPosition getChessPosition() {
		
		return ChassPosition.fromPosition(position);
	}
	protected boolean isThereOpponentPiece(Position position) {
		
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p != null && p.getColor() != this.color;
	}
}
