package com.example.nmm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.nmm.database.RoomDB;
import com.example.nmm.models.Scores;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

enum GameStage {
    Setting, Playing, Deleting, End
}

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private TextView playerTurn;

    private int playerRocks1 = 9;
    private int playerRocks2 = 9;

    static int playerScore1 = 0;
    static int playerScore2 = 0;

    private BoardView boardView;
    private GameStage gameStage;

    private Button saveButton;

    RoomDB database;
    boolean turnPlayer1 = true;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerTurn = findViewById(R.id.playerTurn);
        boardView = findViewById(R.id.board_view);

        Button retreat = findViewById(R.id.retreat_button);
        retreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameStage = GameStage.End;
                displayEnd(turnPlayer1);
            }
        });
        ImageView returnButton = findViewById(R.id.return_from_main_activity);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        saveButton = findViewById(R.id.save_results);
        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setEnabled(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScoresTakerActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        database = RoomDB.getInstance(this);

        boardView.setOnTouchListener(this);
        gameStage = GameStage.Setting;

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Scores newScores = (Scores) data.getSerializableExtra("score");
                database.mainDAO().insert(newScores);

                String email = mAuth.getCurrentUser().getEmail();
                if (email != null) {
                    User user = new User(email, database.mainDAO().getAll());
                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(mAuth.getCurrentUser().getUid())
                            .setValue(user);
                }

                startActivity(new Intent(MainActivity.this, ScoreTable.class));
            }
        }
    }

    private boolean playerHasNot(ArrayList<PointF> player, PointF place) {
        for (int i = 0; i < player.size(); ++i) {
            if (player.get(i).equals(place)) {
                return false;
            }
        }
        return true;
    }

    private boolean placeIsVacant(PointF place) {
        return playerHasNot(boardView.player1, place) && playerHasNot(boardView.player2, place);
    }

    private void displayTurn(boolean firstPlayerTurn) {
        if (firstPlayerTurn) {
            String red = "Red";
            playerTurn.setText(red);
            playerTurn.setTextColor(Color.RED);
        } else {
            String blue = "Blue";
            playerTurn.setText(blue);
            playerTurn.setTextColor(Color.BLUE);
        }
    }

    private void displayEnd(boolean firstPlayerTurn) {
        String winner = getString(R.string.winner);
        TextView view = findViewById(R.id.turn);
        view.setText(winner);
        displayTurn(!firstPlayerTurn);
        saveButton.setVisibility(View.VISIBLE);
        saveButton.setEnabled(true);
    }

    private void getCollectionsByIndex(ArrayList<ArrayList<Integer>> collections,
                                       int index,
                                       ArrayList<ArrayList<Integer>> output) {
        for (int i = 0; i < collections.size(); ++i) {
            if (collections.get(i).contains(index)) {
                output.add(collections.get(i));
            }
        }
    }

    private ArrayList<PointF> findMill(ArrayList<ArrayList<Integer>> rows, ArrayList<PointF> player) {
        ArrayList<PointF> mill = new ArrayList<PointF>();

        for (int i = 0; i < rows.size(); ++i) {
            int playersPoints = 0;
            for (int j = 0; j < rows.get(i).size(); ++j) {
                PointF point = boardView.board.get(rows.get(i).get(j));
                if (player.contains(point)) {
                    playersPoints += 1;
                    if (playersPoints == rows.get(i).size()) {
                        for (int k = 0; k < rows.get(i).size(); ++k) {
                            PointF millPoint = boardView.board.get(rows.get(i).get(k));
                            mill.add(millPoint);
                        }
                        return mill;
                    }
                }
            }
        }

        return mill;
    }

    private ArrayList<PointF> findMillAt(PointF put, ArrayList<PointF> player) {
        ArrayList<PointF> board = boardView.board;
        ArrayList<ArrayList<Integer>> connections = boardView.connections;
        int indexPut = board.indexOf(put);

        ArrayList<ArrayList<Integer>> putRows = new ArrayList<ArrayList<Integer>>();
        getCollectionsByIndex(connections, indexPut, putRows);

        return findMill(putRows, player);
    }

    private boolean checkMillInRow(ArrayList<Integer> row, ArrayList<PointF> player) {
        int foundInPlayer = 0;

        for (int i = 0; i < row.size(); ++i) {
            PointF point = boardView.board.get(row.get(i));
            if (player.contains(point)) {
                foundInPlayer += 1;
            }
        }
        return foundInPlayer == 3;
    }

    private ArrayList<PointF> getNonMills(ArrayList<PointF> player) {
        ArrayList<ArrayList<Integer>> connections = boardView.connections;
        ArrayList<PointF> nonMills = new ArrayList<PointF>(player);

        for (int i = 0; i < player.size(); ++i) {
            if (!nonMills.contains(player.get(i))) {
                continue;
            }
            int figureIndex = boardView.board.indexOf(player.get(i));
            ArrayList<ArrayList<Integer>> connectedRows = new ArrayList<ArrayList<Integer>>();
            getCollectionsByIndex(connections, figureIndex, connectedRows);

            for (int j = 0; j < connectedRows.size(); ++j) {
                if (checkMillInRow(connectedRows.get(j), player)) {
                    for (int k = 0; k < connectedRows.get(j).size(); ++k) {
                        int index = connectedRows.get(j).get(k);
                        PointF point = boardView.board.get(index);
                        nonMills.remove(point);
                    }
                }
            }
        }

        return nonMills;
    }

    private boolean canRelocateInRow(PointF newPos, PointF oldPos) {
        int indexNew = boardView.board.indexOf(newPos);
        int indexOld = boardView.board.indexOf(oldPos);

        for (int i = 0; i < boardView.connections.size(); ++i) {
            if (boardView.connections.get(i).contains(indexNew) &&
                    boardView.connections.get(i).contains(indexOld)) {
                return true;
            }
        }
        return false;
    }

    private boolean canRelocateToNearest(PointF newPos, PointF oldPos) {
        int indexNew = boardView.board.indexOf(newPos);
        int indexOld = boardView.board.indexOf(oldPos);

        for (int i = 0; i < boardView.connections.size(); ++i) {
            if (boardView.connections.get(i).contains(indexNew) &&
                    boardView.connections.get(i).contains(indexOld)) {
                int indexNewInRow = boardView.connections.get(i).indexOf(indexNew);
                int indexOldInRow = boardView.connections.get(i).indexOf(indexOld);
                if (Math.abs(indexNewInRow - indexOldInRow) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setFigureCheckMill(ArrayList<PointF> player, PointF figure) {
        player.add(new PointF(figure.x, figure.y));
        ArrayList<PointF> mill = findMillAt(figure, player);
        if (!mill.isEmpty() && mill.size() == boardView.POINTS_IN_ROW) {
            gameStage = GameStage.Deleting;

            float leftC = mill.get(0).x;
            float topC = mill.get(0).y;
            float rightC = mill.get(0).x;
            float bottomC = mill.get(0).y;
            for (int i = 1; i < mill.size(); ++i) {
                if (mill.get(i).x < leftC) leftC = mill.get(i).x;
                if (mill.get(i).x > rightC) rightC = mill.get(i).x;
                if (mill.get(i).y < topC) topC = mill.get(i).y;
                if (mill.get(i).y > bottomC) bottomC = mill.get(i).y;
            }

            if (leftC != mill.get(0).x || topC != mill.get(0).y ||
                rightC != mill.get(0).x || bottomC != mill.get(0).y) {
                float left = leftC - boardView.FIGURE_RADIUS;
                float top = topC - boardView.FIGURE_RADIUS;
                float right = rightC + boardView.FIGURE_RADIUS;
                float bottom = bottomC + boardView.FIGURE_RADIUS;
                boardView.mill.set(left, top, right, bottom);
            }
        } else {
            turnPlayer1 = player != boardView.player1;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            PointF down = new PointF(x, y);
            PointF closest = BoardView.closestPoint(down, boardView.board);

            if (Calculator.euclideanDistance(closest, down) > 50F) {
                return false;
            }

            switch (gameStage) {
                case Setting:
                    if (turnPlayer1 && placeIsVacant(closest)) {
                        setFigureCheckMill(boardView.player1, closest);
                        playerRocks1 -= 1;
                    } else if (!turnPlayer1 && placeIsVacant(closest)) {
                        setFigureCheckMill(boardView.player2, closest);
                        playerRocks2 -= 1;
                    }

                    if (playerRocks1 == 0 && playerRocks2 == 0 && gameStage != GameStage.Deleting) {
                        gameStage = GameStage.Playing;
                    }
                    break;
                case Playing:
                    if ((turnPlayer1 && !playerHasNot(boardView.player1, closest)) ||
                            (!turnPlayer1 && !playerHasNot(boardView.player2, closest))) {
                            boardView.chosenPoint = closest;
                    } else if (!boardView.chosenPoint.equals(0F, 0F) && placeIsVacant(closest) &&
                                canRelocateToNearest(closest, boardView.chosenPoint)) {
                        if (turnPlayer1) {
                            boardView.player1.remove(boardView.chosenPoint);
                            setFigureCheckMill(boardView.player1, closest);
                        } else {
                            boardView.player2.remove(boardView.chosenPoint);
                            setFigureCheckMill(boardView.player2, closest);
                        }
                        boardView.chosenPoint.set(0F, 0F);
                    }
                    break;
                case Deleting:
                    if (turnPlayer1 &&
                            !playerHasNot(boardView.player2, closest) &&
                            (getNonMills(boardView.player2).contains(closest) || getNonMills(boardView.player2).isEmpty())) {
                        boardView.player2.remove(closest);
                        playerScore1 += 1;
                        boardView.mill.setEmpty();
                        turnPlayer1 = false;
                        if (playerRocks1 != 0 || playerRocks2 != 0) {
                            gameStage = GameStage.Setting;
                        } else if (boardView.player2.size() != 2) {
                            gameStage = GameStage.Playing;
                        } else {
                            gameStage = GameStage.End;
                        }
                    } else if (!turnPlayer1 &&
                            !playerHasNot(boardView.player1, closest) &&
                            (getNonMills(boardView.player1).contains(closest) || getNonMills(boardView.player1).isEmpty())) {
                        boardView.player1.remove(closest);
                        playerScore2 += 1;
                        boardView.mill.setEmpty();
                        turnPlayer1 = true;
                        if (playerRocks1 != 0 || playerRocks2 != 0) {
                            gameStage = GameStage.Setting;
                        } else if (boardView.player1.size() != 2) {
                            gameStage = GameStage.Playing;
                        } else {
                            gameStage = GameStage.End;
                        }
                    }
                    break;
            }
        }
        if (gameStage != GameStage.End) {
            displayTurn(turnPlayer1);
        } else {
            displayEnd(turnPlayer1);
        }
        view.invalidate();
        return true;
    }
}