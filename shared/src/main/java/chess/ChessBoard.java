package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] pieces = new ChessPiece[8][8];

    public ChessBoard() {
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();

        pieces[row - 1][col - 1] = piece;
    }

    /**
     * moves a chess piece on the chessboard
     *
     * @param from where the piece is coming from
     * @param to   where to put the piece
     */
    public void movePiece(ChessPosition from, ChessPosition to) {
        pieces[to.getRow()][to.getColumn()] = pieces[from.getRow()][from.getColumn()];
        pieces[from.getRow()][from.getColumn()] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     *         position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();

        return pieces[row - 1][col - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece[][] newPieces = {
                {
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)
                },
                {
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)
                },
                { null, null, null, null, null, null, null, null },
                { null, null, null, null, null, null, null, null },
                { null, null, null, null, null, null, null, null },
                { null, null, null, null, null, null, null, null },
                {
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)
                },
                {
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)
                }
        };

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                pieces[row][col] = newPieces[row][col];
            }
        }
    }

    @Override

    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        ChessBoard that = (ChessBoard) obj;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece thisPiece = pieces[row][col];
                ChessPiece thatPiece = that.pieces[row][col];
                if (thisPiece == null && thatPiece == null) {
                    continue;
                }
                if (!thisPiece.equals(thatPiece)) {
                    return false;
                }
            }
        }

        return true;

    }
}
