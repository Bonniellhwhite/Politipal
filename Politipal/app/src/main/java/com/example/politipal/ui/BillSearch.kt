
package com.example.politipal.ui

import android.content.ContentValues
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature
import com.example.politipal.ui.utils.PolitipalContentType
import com.example.politipal.ui.utils.PolitipalNavigationType

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.politipal.R
import com.example.politipal.data.Bill
import com.example.politipal.data.BillFilterOptions


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BillSearch(
    contentType: PolitipalContentType,
    homeUIState: homeUIState,
    navigationType: PolitipalNavigationType,
    displayFeatures: List<DisplayFeature>,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, PolitipalContentType) -> Unit,     // Function to nav to detail page
    toggleSelectedRep: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dummyBill = Bill(
        number = 101,
        originChamber = "Senate",
        originChamberCode = "SEN",
        title = "Climate Change Act 2024",
        type = "Bill",
        updateDate = "2024-05-14",
        updateDateIncludingText = "Last updated on May 14, 2024",
        url = "https://www.legislation.gov/senate/bill/101"
    )
    val viewModel = HomeViewModel()
    var selectedBill by remember { mutableStateOf(dummyBill) }

    val updateSelectedBill = { newBill: Bill ->
        selectedBill = newBill
    }

    var showBill by remember { mutableStateOf(false) }
    val updateShowBill = { newShow: Boolean ->
        showBill = newShow
    }

    Surface(modifier = Modifier.fillMaxWidth()) {
        if (showBill) { // Where does this come from?
            BillPage(contentType = contentType, homeUIState = homeUIState, bill = selectedBill)
        } else {
            BillSearchBar(
                modifier = modifier,
                viewModel = viewModel,
                homeUIState = homeUIState,
                updateShowBill = updateShowBill,
                updateSelectedBill = updateSelectedBill
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun BillSearchBar(
    modifier: Modifier,
    viewModel: HomeViewModel,
    homeUIState: homeUIState,
    updateShowBill: (Boolean) -> Unit,
    updateSelectedBill: (Bill) -> Unit
){
    // Variables for component
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val selectedFilters = remember { mutableStateOf(setOf<BillFilterOptions>())}
    var boxVisible by remember {
        mutableStateOf(false)
    }


    // Inner Component layout for just search bar
    Box(modifier = modifier
        .fillMaxWidth()
        .background(Color(0xFFF9F1F8))
        .padding(bottom = 5.dp, top = 40.dp, start = 10.dp, end = 10.dp)
    ) {
        LazyColumn (){
            stickyHeader {
                Surface(
                    Modifier
                        .fillParentMaxWidth()
                ) {
                    Row(modifier.background(Color(0xFFF9F1F8))) {
                        DockedSearchBar(
                            modifier = Modifier
                                .wrapContentWidth()
                                .width(330.dp)
                                .padding(top = 10.dp,bottom = 10.dp),
                            query = text,
                            onQueryChange = {
                                text = it
                            },
                            onSearch = {
                                keyboardController?.hide()
                                text = ""
                            },
                            active = false,
                            onActiveChange = { active = it },
                            placeholder = { Text(text = "Search Bills") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon"
                                )
                            },
                            trailingIcon = {
                                if (active) {
                                    Icon(
                                        modifier = Modifier.clickable {
                                            keyboardController?.hide()
                                            text = ""
                                        },
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Icon"
                                    )
                                }
                            }

                        ) {}

                        // Filter Button
                        FloatingActionButton(
                            onClick = { boxVisible = !boxVisible },
                            modifier = Modifier
                                .padding(top = 10.dp,bottom = 10.dp, start = 5.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Favorite",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }

                    }

                }
                // Filters Sections
                AnimatedVisibility(visible = boxVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFF0E5F4),
                                RoundedCornerShape(15.dp)
                            ) // Box styling

                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Demograhics",
                                    fontSize = 16.sp, // Set font size
                                    fontWeight = FontWeight.Bold, // Set font weight for header
                                    modifier = Modifier.padding(5.dp) // Optionally, add padding around the text
                                )
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = Color.Gray
                                )

                                // Chips Here
                                FlowRow(
                                    modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    // Chips
                                    BillFilterChip(
                                        modifier = modifier,
                                        label = "House",
                                        selectedFilters = selectedFilters,
                                        billFilterOption = BillFilterOptions.HOUSE
                                    )
                                    BillFilterChip(
                                        modifier = modifier,
                                        label = "Senate",
                                        selectedFilters = selectedFilters,
                                        billFilterOption = BillFilterOptions.SENATE
                                    )

                                }

                                /* No reply Button Needed
                                Button(
                                    onClick = {
                                        // var filterStatus = FilterOptions.
                                        // viewModel.toggleFilter(filterStatus)
                                        boxVisible = false
                                    }, modifier = Modifier.align(
                                        Alignment.End
                                    )
                                ) {
                                    Text(text = "Apply")
                                }*/
                            }
                        }
                    }
                }
            }
            //val state by viewModel.homeUIState.collectAsState()

            val bills = homeUIState.bills
            items(items = bills.filter{it.contains(text,selectedFilters)}) { bill ->
                BillResultListView(
                    modifier = modifier,
                    bill = bill,
                    updateShowBill = updateShowBill,
                    updateSelectedBill = updateSelectedBill
                    //toggleRepSelection = toggleSelectedRep,
                )
            }
        }
    }
}

@Composable
fun BillResultListView(
    modifier: Modifier,
    bill: Bill,
    //toggleRepSelection: (String) -> Unit,
    updateShowBill: (Boolean) -> Unit,
    updateSelectedBill: (Bill) -> Unit
){
    ElevatedCard(
        onClick = { Log.d(ContentValues.TAG,bill.number.toString())
            updateShowBill(true)
            updateSelectedBill(bill)},
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 4.dp)
            .height(150.dp)
            .wrapContentSize(Alignment.Center)
        ,

        ){
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ){

            if (bill.originChamber == "Senate"){
                Image(painter = painterResource(id = R.drawable.senate),
                    contentDescription = "rep m",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }
            if (bill.originChamber == "House"){
                Image(painter = painterResource(id = R.drawable.house),
                    contentDescription = "rep m",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
            ) {

                Text(text = "Bill: ${bill.number}", style = MaterialTheme.typography.headlineSmall)
                Text(text =  bill.title)
            }
        }
    }
    Spacer(Modifier.height(20.dp))

}


@Composable
fun BillFilterChip(
    modifier: Modifier = Modifier,
    label: String,
    billFilterOption: BillFilterOptions,
    selectedFilters: MutableState<Set<BillFilterOptions>>
){
    //val isSelected = remember { mutableStateOf(false) }
    //var selected by remember { mutableStateOf(false) }
    val isSelected = remember { mutableStateOf(billFilterOption in selectedFilters.value) }
    FilterChip(
        modifier = Modifier.padding( end = 5.dp),
        onClick =
        {
            //selected = !selected

            val currentlySelected = isSelected.value
            isSelected.value = !currentlySelected
            if (isSelected.value) {
                selectedFilters.value += billFilterOption
            } else {
                selectedFilters.value -= billFilterOption
            }
        },

        label = {
            Text(label)
        },
        selected = isSelected.value,
        leadingIcon = if (isSelected.value) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}