package chess;

import java.util.ArrayList;
import java.util.Collection;

import java.util.Iterator;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case KING:
                int[][] kingMoves = {
                        { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                        { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
                };
                for (int[] move : kingMoves) {
                    addMoveIfValid(board, myPosition, moves, move[0], move[1]);
                }
                break;

            case QUEEN:
                addLinearMoves(board, myPosition, moves, new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                        { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } });
                break;

            case BISHOP:
                addLinearMoves(board, myPosition, moves, new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } });
                break;

            case KNIGHT:
                int[][] knightMoves = {
                        { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 },
                        { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 }
                };
                for (int[] move : knightMoves) {
                    addMoveIfValid(board, myPosition, moves, move[0], move[1]);
                }
                break;

            case ROOK:
                addLinearMoves(board, myPosition, moves, new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } });
                break;

            case PAWN:
                int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
                addPawnMoves(board, myPosition, moves, direction);
                break;

            default:
                throw new IllegalArgumentException("Invalid piece type");
        }

        // Remove invalid moves
        Iterator<ChessMove> iterator = moves.iterator();
        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            ChessPosition endPosition = move.getEndPosition();
            if (endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8 ||
                    (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == pieceColor)) {
                iterator.remove();
            }
        }

        return moves;
    }

    private void addMoveIfValid(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int rowChange, int colChange) {
        ChessPosition endPosition = new ChessPosition(myPosition.getRow() + rowChange, myPosition.getColumn() + colChange);
        if (board.getPiece(endPosition) == null || board.getPiece(endPosition).getTeamColor() != pieceColor) {
            moves.add(new ChessMove(myPosition, endPosition));
        }
    }

    private void addLinearMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int[][] directions) {
        for (int[] direction : directions) {
            int rowChange = direction[0];
            int colChange = direction[1];
            ChessPosition endPosition = new ChessPosition(myPosition.getRow() + rowChange, myPosition.getColumn() + colChange);
            while (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition, endPosition));
                endPosition = new ChessPosition(endPosition.getRow() + rowChange, endPosition.getColumn() + colChange);
            }
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, endPosition));
            }
        }
    }

    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int direction) {
        ChessPosition endPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (board.getPiece(endPosition) == null) {
            moves.add(new ChessMove(myPosition, endPosition));
            if (myPosition.getRow() == 2 || myPosition.getRow() == 7) {
                endPosition = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
                if (board.getPiece(endPosition) == null) {
                    moves.add(new ChessMove(myPosition, endPosition));
                }
            }
        }

        for (int colChange : new int[] { -1, 1 }) {
            endPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + colChange);
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, endPosition));
            }
        }

        // Add en passant moves
        // TODO: Implement en passant
    }
}
