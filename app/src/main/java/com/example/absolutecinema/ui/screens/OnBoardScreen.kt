package com.example.absolutecinema.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absolutecinema.R
import com.example.absolutecinema.data.onboarding.OnBoardingModel
import com.example.absolutecinema.ui.theme.Inter
import com.example.absolutecinema.ui.theme.Montserrat
import com.example.absolutecinema.ui.theme.darkBlue
import kotlinx.coroutines.launch

@Composable
fun OnBoardScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnBoardingModel.FirstPage,
        OnBoardingModel.SecondPage,
        OnBoardingModel.ThirdPage,
        OnBoardingModel.FourthPage
    )
    val scope = rememberCoroutineScope()


    val pagerState = rememberPagerState(0) {
        pages.size
    }


    val buttonState= remember{
        derivedStateOf {
            when(pagerState.currentPage){
                0-> listOf("","Next")
                1-> listOf("Back","Next")
                2-> listOf("Back","Next")
                3-> listOf("Back","Get Started")
                else-> listOf("","")
            }
        }
    }

    Scaffold(
        containerColor = darkBlue,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 10.dp).background(darkBlue),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box (modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart){
                    if(buttonState.value[0].isNotEmpty())
                    ButtonUi(
                        text = buttonState.value[0],
                        backgroundColor = Color.Transparent,
                        textColor = Color.White,
                    ) {
                        scope.launch{
                            if(pagerState.currentPage>0){
                                pagerState.animateScrollToPage(pagerState.currentPage-1)
                            }
                        }
                    }
                }


                Box  (modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center){
                    IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
                }

                Box  (modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd){
                    ButtonUi(
                        text = buttonState.value[1],
                        backgroundColor = Color.Red,
                        textColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                        scope.launch {
                            if(pagerState.currentPage<pages.size-1)
                            {
                                pagerState.animateScrollToPage(pagerState.currentPage+1)
                            }
                            else
                            {
                                onFinished()
                            }
                        }
                    }
                }

            }
        },
        content = {
            Column(Modifier.padding(it).background(darkBlue)) {
                HorizontalPager(state = pagerState) { index ->
                    OnBoardingGraph(onBoardingModel = pages[index])
                }
            }
        },
    )
}



@Composable
fun ButtonUi(
    text: String = "Next",
    backgroundColor: Color = Color.Red,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    fontSize: Int = 14,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text, fontSize = fontSize.sp, style = textStyle)
    }
}

@Composable
fun OnBoardingGraph(onBoardingModel: OnBoardingModel) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.size(80.dp))
        Image(
            painter = painterResource(onBoardingModel.image),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp),
            alignment = Alignment.Center
        )
        Spacer(modifier = Modifier.size(60.dp))
        Text(
            onBoardingModel.title,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            letterSpacing = 0.2.sp,
            fontFamily = Montserrat,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.size(50.dp))
        Text(
            onBoardingModel.desc,
            modifier = Modifier.fillMaxWidth()
                .padding(15.dp,0.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            lineHeight = 23.sp
        )
        Spacer(modifier = Modifier.size(50.dp))


    }
}

@Composable
fun IndicatorUI(
    pageSize: Int,
    currentPage: Int,
    selectedColor: Color = Color.Black,
    unselectedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(pageSize) {
            Spacer(modifier = Modifier.size(2.5.dp))
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .width(width = if(it==currentPage) 32.dp else 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = if(it==currentPage) selectedColor else unselectedColor)

            )
            Spacer(modifier = Modifier.size(2.5.dp))
        }

    }

}





@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true, showBackground = true)
@Composable
private fun OnBoardPreview() {
    OnBoardScreen {}
}