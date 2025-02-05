package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    public ChessBoard deepCopy() {
        ChessBoard newBoard = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, newBoard.board[i], 0, 8);
        }
        return newBoard;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    public void movePiece(ChessMove move) {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece.PieceType promo = move.getPromotionPiece();
        ChessPiece piece = getPiece(startPos);

        this.board[startPos.getRow() - 1][startPos.getColumn() - 1] = null;
        this.board[endPos.getRow() - 1][endPos.getColumn() - 1] = piece;
        if (promo != null) {
            this.board[endPos.getRow() - 1][endPos.getColumn() - 1] =
                    new ChessPiece(piece.getTeamColor(), promo);
        }
    }

    public ChessPosition getKingPos(ChessGame.TeamColor color) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                    return new ChessPosition(i, j);
                }
            }
        }
        throw new RuntimeException("Uhh why don't you have a King lol");
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = getPiece(new ChessPosition(i, j));
                if (piece == null) {
                    continue;
                }
                Collection<ChessMove> moves = piece.pieceMoves(this, new ChessPosition(i, j));
                Collection<ChessPosition> endPositions = new ArrayList<>();
                for (ChessMove move : moves) {
                    endPositions.add(move.getEndPosition());
                }
                if (endPositions.contains(this.getKingPos(teamColor))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[][] {
                {
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK),
                },
                {
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                },
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                },
                {
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK),
                }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
