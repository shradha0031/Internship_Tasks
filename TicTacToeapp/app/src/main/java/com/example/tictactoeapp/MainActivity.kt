package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GameScreen()
                }
            }
        }
    }
}

@Composable
fun GameScreen() {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    fun checkWinner(): String? {
        val lines = listOf(
            listOf(board[0][0], board[0][1], board[0][2]),
            listOf(board[1][0], board[1][1], board[1][2]),
            listOf(board[2][0], board[2][1], board[2][2]),
            listOf(board[0][0], board[1][0], board[2][0]),
            listOf(board[0][1], board[1][1], board[2][1]),
            listOf(board[0][2], board[1][2], board[2][2]),
            listOf(board[0][0], board[1][1], board[2][2]),
            listOf(board[0][2], board[1][1], board[2][0])
        )
        for (line in lines) {
            if (line.all { it == "X" }) return "X"
            if (line.all { it == "O" }) return "O"
        }
        return if (board.all { row -> row.all { it.isNotEmpty() } }) "Draw" else null
    }

    fun resetBoard() {
        board = List(3) { MutableList(3) { "" } }
        currentPlayer = "X"
        winner = null
        selectedCell = null
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = winner?.let { if (it == "Draw") "It's a Draw!" else "$it Wins!" }
                ?: "Turn: $currentPlayer",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Column {
            for (i in 0..2) {
                Row {
                    for (j in 0..2) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                                .background(Color(0xFFe0f7fa), RoundedCornerShape(12.dp))
                                .clickable(enabled = board[i][j].isEmpty() && winner == null) {
                                    selectedCell = Pair(i, j)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = board[i][j],
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (board[i][j] == "X") Color(0xFF00695C) else Color(0xFFD84315)
                            )
                        }
                    }
                }
            }
        }

        if (selectedCell != null && board[selectedCell!!.first][selectedCell!!.second] == "") {
            Dialog(
                onDismissRequest = { selectedCell = null },
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
            ) {
                Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 4.dp) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Choose Symbol", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf("X", "O").forEach { symbol ->
                                Button(onClick = {
                                    val (i, j) = selectedCell!!
                                    board = board.toMutableList().also {
                                        it[i] = it[i].toMutableList().also { row ->
                                            row[j] = symbol
                                        }
                                    }
                                    winner = checkWinner()
                                    if (winner == null) {
                                        currentPlayer = if (currentPlayer == "X") "O" else "X"
                                    }
                                    selectedCell = null
                                }) {
                                    Text(symbol, fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { resetBoard() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Reset", color = Color.White, fontSize = 18.sp)
        }
    }
}
