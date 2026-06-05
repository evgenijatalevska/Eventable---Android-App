package com.example.eventable.ui

import android.os.Bundle // ДОДАДЕНО ЗА ANALYTICS
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // ДОДАДЕНО ЗА ANALYTICS
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventable.R
import com.example.eventable.data.Offer
import com.example.eventable.ui.theme.*
import com.google.firebase.analytics.FirebaseAnalytics // ДОДАДЕНО ЗА ANALYTICS

@Composable
fun OffersScreen(
    offers: List<Offer>,
    isLoading: Boolean,
    onOfferClick: (String) -> Unit,
    onAddOfferClick: () -> Unit
) {
    // ДОДАДЕНО ЗА ANALYTICS
    val context = LocalContext.current
    val analytics = remember { FirebaseAnalytics.getInstance(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- ХЕДЕР СЕКЦИЈА СО НАСЛОВ И ПРЕКЛОПЕНА ПРВА КАРТИЧКА ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Малку понизок бидејќи нема копче за календар
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.events_header), // Можеш да ставиш и R.drawable.offers_header ако имаш посебна слика
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            PastelGreenDark.copy(alpha = 0.6f),
                                            PastelGreenPrimary.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                        )

                        // Наслов „Понуди“ спуштен на исто ниво како кај Настани
                        Text(
                            text = "Понуди",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 20.dp, top = 40.dp)
                        )
                    }

                    // Првата картичка се преклопува на хедeрот
                    if (offers.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(top = 140.dp)
                        ) {
                            OfferCardWhite(
                                offer = offers.first(),
                                onClick = {
                                    // ДОДАДЕНО ЗА ANALYTICS
                                    val bundle = Bundle().apply { putString("offer_id", offers.first().id) }
                                    analytics.logEvent("view_offer_detail", bundle)
                                    onOfferClick(offers.first().id)
                                }
                            )
                        }
                    }
                }
            }

            // --- ОСТАНАТИ ПОНУДИ ОД ЛИСТАТА ---
            if (offers.isNotEmpty()) {
                items(offers.drop(1)) { offer ->
                    OfferCardWhite(
                        offer = offer,
                        onClick = {
                            // ДОДАДЕНО ЗА ANALYTICS
                            val bundle = Bundle().apply { putString("offer_id", offer.id) }
                            analytics.logEvent("view_offer_detail", bundle)
                            onOfferClick(offer.id)
                        }
                    )
                }
            } else if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PastelGreenDark)
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📑", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Нема зачувани понуди",
                            fontSize = 16.sp,
                            color = TextDark.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Притисни + за да креираш нова понуда",
                            fontSize = 13.sp,
                            color = TextDark.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Десно долу копче за додавање понуда
        FloatingActionButton(
            onClick = {
                // ДОДАДЕНО ЗА ANALYTICS
                analytics.logEvent("add_offer_fab_click", null)
                onAddOfferClick()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PastelGreenPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Додај понуда")
        }
    }
}

@Composable
fun OfferCardWhite(
    offer: Offer,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = PastelGreenDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = offer.title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            if (offer.content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = offer.content,
                    fontSize = 14.sp,
                    color = TextDark.copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 28.dp)
                )
            }
        }
    }
}