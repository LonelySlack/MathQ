package com.example.mathq.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// ✅ FIX: All imports are now from 'androidx.compose.material3'
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset // Specific import for clarity
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mathq.R
import com.example.mathq.common.AppBackground
import com.example.mathq.common.BottomNavBar
import com.example.mathq.data.AuthRepository
import com.example.mathq.models.LeaderboardUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavController,
    userId: String,
    onBackClick: () -> Unit
) {
    var easyList by remember { mutableStateOf<List<LeaderboardUser>>(emptyList()) }
    var normalList by remember { mutableStateOf<List<LeaderboardUser>>(emptyList()) }
    var hardList by remember { mutableStateOf<List<LeaderboardUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        isLoading = true
        AuthRepository.getLeaderboardData { easy, normal, hard ->
            easyList = easy
            normalList = normal
            hardList = hard
            isLoading = false
        }
    }

    val tabs = listOf("Easy", "Normal", "Hard")
    val currentList = when (selectedTabIndex) {
        0 -> easyList
        1 -> normalList
        else -> hardList
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, currentUserId = userId) }
    ) { paddingValues ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Leaderboard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, style = MaterialTheme.typography.titleMedium) }
                        )
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                    }
                } else {
                    LeaderboardContent(users = currentList)
                }
            }
        }
    }
}

@Composable
fun LeaderboardContent(users: List<LeaderboardUser>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No scores yet for this mode!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        } else {
            TopPlayers(users = users.take(3))
            Spacer(modifier = Modifier.height(24.dp))
            if (users.size > 3) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        users.drop(3).forEachIndexed { index, user ->
                            LeaderboardRow(rank = index + 4, name = user.name, score = user.score)
                            if (index < users.drop(3).lastIndex) {
                                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopPlayers(users: List<LeaderboardUser>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        if (users.size > 1) {
            TopPlayerItem(user = users[1], rank = 2)
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        if (users.isNotEmpty()) {
            TopPlayerItem(user = users[0], rank = 1)
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        if (users.size > 2) {
            TopPlayerItem(user = users[2], rank = 3)
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TopPlayerItem(user: LeaderboardUser, rank: Int) {
    val height = when (rank) {
        1 -> 150.dp
        2 -> 120.dp
        else -> 100.dp
    }
    Column(
        modifier = Modifier.height(height),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val medal = when (rank) {
            1 -> R.drawable.ic_gold_medal
            2 -> R.drawable.ic_silver_medal
            else -> R.drawable.ic_bronze_medal
        }
        Image(
            painter = painterResource(id = medal),
            contentDescription = "Rank $rank",
            modifier = Modifier.size(40.dp)
        )
        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, color = MaterialTheme.colorScheme.onBackground)
        Text("${user.score} pts", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
    }
}

@Composable
fun LeaderboardRow(rank: Int, name: String, score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rank",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
