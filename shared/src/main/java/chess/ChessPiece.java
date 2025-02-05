package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private Collection<ChessMove> moves;
    private int row;
    private int col;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        moves = null;
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
        moves = new ArrayList<>();
        row = myPosition.getRow();
        col = myPosition.getColumn();

        switch (type) {
            case KING -> {
                return kingMoves(board, myPosition);
            }
            case QUEEN -> {
                return queenMoves(board, myPosition);
            }
            case ROOK -> {
                return rookMoves(board, myPosition);
            }
            case BISHOP -> {
                return bishopMoves(board, myPosition);
            }
            case KNIGHT -> {
                return knightMoves(board, myPosition);
            }
            case PAWN -> {
                return pawnMoves(board, myPosition);
            }
            default -> throw new RuntimeException("Invalid Piece Type");
        }
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                ChessPosition endPosition = new ChessPosition(row + i, col + j);
                if (endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8) {
                    continue;
                }
                if (board.getPiece(endPosition) == null ||
                        board.getPiece(endPosition).getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, endPosition, null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int distance = 1;
                while (true) {
                    ChessPosition endPosition = new ChessPosition(row + (i * distance), col + (j * distance));
                    if (endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8) {
                        break;
                    }
                    if (board.getPiece(endPosition) == null) {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                        distance++;
                    } else if (board.getPiece(endPosition).getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 || j == 0) {
                    int distance = 1;
                    while (true) {
                        ChessPosition endPosition = new ChessPosition(row + (i * distance), col + (j * distance));
                        if (endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8) {
                            break;
                        }
                        if (board.getPiece(endPosition) == null) {
                            moves.add(new ChessMove(myPosition, endPosition, null));
                            distance++;
                        } else if (board.getPiece(endPosition).getTeamColor() != pieceColor) {
                            moves.add(new ChessMove(myPosition, endPosition, null));
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 || j == 0) {
                    continue;
                }
                int distance = 1;
                while (true) {
                    ChessPosition endPosition = new ChessPosition(row + (i * distance), col + (j * distance));
                    if (endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8) {
                        break;
                    }
                    if (board.getPiece(endPosition) == null) {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                        distance++;
                    } else if (board.getPiece(endPosition).getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                if (!((i == 2 || i == -2) && (j == 1 || j == -1))) {
                    if (!((i == 1 || i == -1) && (j == 2 || j == -2))) {
                        continue;
                    }
                }
                ChessPosition endPosition = new ChessPosition(row + i, col + j);
                if (endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8) {
                    continue;
                }
                if (board.getPiece(endPosition) == null ||
                        board.getPiece(endPosition).getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, endPosition, null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        if (pieceColor == ChessGame.TeamColor.WHITE) {

            // forward
            ChessPosition endPosition = new ChessPosition(row + 1, col);
            if (!(endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8)) {
                if (board.getPiece(endPosition) == null) {

                    // check promotion
                    if (endPosition.getRow() == 8) {
                        moves.addAll(promotionMoves(myPosition, endPosition));
                    } else {
                        moves.add(new ChessMove(myPosition, endPosition, null));

                        // double forward
                        if (myPosition.getRow() == 2) {
                            endPosition = new ChessPosition(row + 2, col);
                            if (!(endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8)) {
                                if (board.getPiece(endPosition) == null) {
                                    moves.add(new ChessMove(myPosition, endPosition, null));
                                }
                            }
                        }
                    }
                }
            }

            // attack
            endPosition = new ChessPosition(row + 1, col + 1);
            if (!(endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8)) {
                if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (endPosition.getRow() == 8) {
                        moves.addAll(promotionMoves(myPosition, endPosition));
                    } else {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
            }
            endPosition = new ChessPosition(row + 1, col - 1);
            if (!(endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8)) {
                if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (endPosition.getRow() == 8) {
                        moves.addAll(promotionMoves(myPosition, endPosition));
                    } else {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
            }
        } else {
            // BLACK
            // forward
            ChessPosition endPosition = new ChessPosition(row - 1, col);
            if (!(endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8)) {
                if (board.getPiece(endPosition) == null) {

                    // check promotion
                    if (endPosition.getRow() == 1) {
                        moves.addAll(promotionMoves(myPosition, endPosition));
                    } else {
                        moves.add(new ChessMove(myPosition, endPosition, null));

                        // double forward
                        if (myPosition.getRow() == 7) {
                            endPosition = new ChessPosition(row - 2, col);
                            if (!(endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8)) {
                                if (board.getPiece(endPosition) == null) {
                                    moves.add(new ChessMove(myPosition, endPosition, null));
                                }
                            }
                        }
                    }
                }
            }

            // attack
            endPosition = new ChessPosition(row - 1, col + 1);
            if (!(endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8)) {
                if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (endPosition.getRow() == 1) {
                        moves.addAll(promotionMoves(myPosition, endPosition));
                    } else {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
            }
            endPosition = new ChessPosition(row - 1, col - 1);
            if (!(endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() < 1 || endPosition.getColumn() > 8)) {
                if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (endPosition.getRow() == 1) {
                        moves.addAll(promotionMoves(myPosition, endPosition));
                    } else {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> promotionMoves(ChessPosition myPosition, ChessPosition endPosition) {
        Collection<ChessMove> promoMoves = new ArrayList<>();

        moves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
        moves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));

        return promoMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
