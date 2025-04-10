package com.someone.super_contact_app.ui.screens.contact_detail.component

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.someone.super_contact_app.R
import com.someone.super_contact_app.model.PhoneNumber
import com.someone.super_contact_app.model.PhoneNumberType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneNumberCard(
    phones: List<PhoneNumber>,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceContainerLow)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Contact info",
                style = typography.titleMedium.copy(fontSize = 17.sp),
                modifier = Modifier.padding(16.dp)
            )
            phones.forEachIndexed { index, it ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.combinedClickable(
                        onLongClick = {
                            clipboard.setText(AnnotatedString(it.number))
                            Toast.makeText(context, "Copied phone number", Toast.LENGTH_SHORT).show()
                        },
                        onLongClickLabel = "Copy phone number",
                        onClick = {
                            // TODO: call
                        }
                    ).padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Outlined.Call, null)
                    Column(Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .weight(1f)
                    ) {
                        Text(it.number)
                        Text(it.type.name, color = colorScheme.outline, fontSize = 14.sp)
                    }
                    IconButton(onClick = {}) {
                        Icon(painterResource(R.drawable.forum_24px), "Send message")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PNCardPrev() {
    PhoneNumberCard(
        phones = listOf(
            PhoneNumber("123123123", PhoneNumberType.Home),
            PhoneNumber("765432111", PhoneNumberType.Work)
        ),
    )
}