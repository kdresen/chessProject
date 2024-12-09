package ui;

import chess.*;
import ui.server.ServerFacade;
import ui.websocket.WebsocketCommunicator;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.util.Objects;
import java.util.Scanner;

public class GameClient {
    private final Scanner scanner;
    private final ChessGame.TeamColor color;
    private final WebsocketCommunicator websocketCommunicator;
    private final String authToken;
    private final int gameID;
    private final ServerFacade server;
    private ChessGame chessGame;
    private static final boolean CLOSED = false;


    public GameClient(Client client, ServerFacade serverFacade, int gameID, ChessGame.TeamColor teamColor, String authToken) throws Exception {
        scanner = new Scanner(System.in);
        color = teamColor;
        this.authToken = authToken;
        this.gameID = gameID;
        this.server = serverFacade;
        try {
            this.websocketCommunicator = client.getServer().createWebSocketClient(this);
        } catch (Exception e) {
            throw new Exception("Error creating web socket client");
        }
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            websocketCommunicator.sendMessage(command);
        } catch (Exception e) {
            throw new Exception("Error joining web socket server");
        }
        websocketCommunicator.teamColor = Objects.requireNonNullElse(teamColor, ChessGame.TeamColor.WHITE);
    }

    public void start() {
        System.out.println("Welcome to the chess game, type 'help' for a list of commands.");

        while (true) {
            if (CLOSED) {
                return;
            }
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.print("[IN_GAME] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "redraw":
                    ChessGame.TeamColor playerColor = (color == null) ? ChessGame.TeamColor.WHITE : color;
                    DrawChessBoard.drawBoard(chessGame.getBoard(), playerColor, null);
                    break;
                case "leave":
                    if (leaveGame()) {
                        return;
                    }
                    break;
                case "move":
                    if (color == null) {
                        System.out.println("You cannot make moves as an " +
                                "observer you backseat driver you");
                        break;
                    }
                    makeMove();
                    break;
                case "resign":
                    if (color == null) {
                        System.out.println("You cannot resign as an observer");
                        break;
                    }
                    resign();
                    break;
                case "show":
                    showLegalMoves();
                    break;
                default:
                    help();
                    break;
            }
        }
    }

    private void help() {
        System.out.println("""
                        redraw chess board:     redraw
                        show legal moves;       show
                        make a move:            move
                        leave the game:         leave
                        resign the game:        resign
                    """);
    }

    private Boolean leaveGame() {
        System.out.print("Do you want to leave the game? (y/n)");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("y") || response.equals("yes")){
            UserGameCommand leave = new UserGameCommand(UserGameCommand.CommandType.LEAVE,
                    authToken, gameID);

            websocketCommunicator.sendMessage(leave);
            System.out.println("You left the game");
            return true;
        } else {
            System.out.println("Canceled");
            return false;
        }
    }

    private void makeMove() {
        if (this.chessGame.getGameOver()) {
            System.out.println("Game is over");
            return;
        }
        if (!this.chessGame.getTeamTurn().equals(color)) {
            System.out.println("You can't make a move when it isn't your turn");
            return;
        }
        System.out.print("""
                Enter the position of the piece you want to move (example: e6) :
                """);
        ChessPosition s = parsePosition(scanner.nextLine().trim());
        if (s == null) {
            return;
        }
        ChessPiece piece = chessGame.getBoard().getPiece(s);
        if (piece == null || piece.getTeamColor() != this.color) {
            System.out.println("Invalid piece");
            return;
        }
        System.out.print("Enter the position you want to move to (example d5) :");
        ChessPosition e = parsePosition(scanner.nextLine().trim());
        if (e == null) {
            return;
        }

        ChessPiece.PieceType promotion = null;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (((color == ChessGame.TeamColor.BLACK) && (e.getRow() == 1)) ||
                    ((color == ChessGame.TeamColor.WHITE) && (e.getRow() == 8))) {
                System.out.print("Enter the promotion piece (example: rook): ");
                switch (scanner.nextLine().trim()) {
                    case "queen":
                        promotion = ChessPiece.PieceType.QUEEN;
                        break;
                    case "rook":
                        promotion = ChessPiece.PieceType.ROOK;
                        break;
                    case "knight":
                        promotion = ChessPiece.PieceType.KNIGHT;
                        break;
                    case "bishop":
                        promotion = ChessPiece.PieceType.BISHOP;
                        break;
                    default:
                        System.out.println("Invalid promotion piece");
                        makeMove();
                }
            }
        }
        ChessMove move = new ChessMove(s, e, promotion);
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException ex) {
            System.out.println("Invalid move");
            return;
        }

        try {
            UserGameCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE
                    , authToken, gameID, move);
            websocketCommunicator.sendMessage(command);

        } catch (IllegalArgumentException ex) {
            System.out.println("Unable to make move: " + ex.getMessage());
        }
        System.out.print("[IN_GAME] >>> ");
    }

    public void resign() {
        System.out.print("Do you want to resign? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("y") || response.equals("yes")){
            UserGameCommand resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            websocketCommunicator.sendMessage(resign);
            System.out.println("You resigned the game");
        } else {
            System.out.println("Canceled");
        }
        System.out.print("[IN_GAME] >>> ");
    }

    private void showLegalMoves() {
        System.out.print("Enter the position of the piece you want to move (example: e6): ");
        ChessPosition p = parsePosition(scanner.nextLine().trim());
        if (p != null) {
            ChessGame.TeamColor playerColor = (color == null) ? ChessGame.TeamColor.WHITE : color;
            DrawChessBoard.printHightlights(chessGame.getBoard(), playerColor, chessGame.validMoves(p));
        } else {
            System.out.println("Invalid position");
        }
    }

    private ChessPosition parsePosition(String input) {
        if (input.length() == 2) {
            int col = input.charAt(0) - 'a' + 1;
            int row = input.charAt(1) - '1' + 1;
            if (col > 0 && col < 9 && row > 0 && row < 9) {
                return new ChessPosition(row, col);
            } else {
                System.out.println("Please use the format [a-h][1-8].");
                return null;
            }
        } else {
            System.out.println("Please use the format [a-h][1-8].");
            return null;
        }
    }

    public void loadGame(ChessGame game) {
        this.chessGame = game;
        ChessGame.TeamColor playerColor = (color == null) ? ChessGame.TeamColor.WHITE : color;
        DrawChessBoard.drawBoard(chessGame.getBoard(), playerColor, null);
        System.out.println("Game updated");
        System.out.print("[IN_GAME] >>> ");
    }
}
