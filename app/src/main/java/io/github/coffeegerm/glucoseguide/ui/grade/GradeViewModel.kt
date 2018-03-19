/*
 * Copyright 2018 Coffee and Cream Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.coffeegerm.glucoseguide.ui.grade

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.github.coffeegerm.glucoseguide.data.DatabaseManager

/**
 * TODO: Add class comment header
 */

class GradeViewModel(private var databaseManager: DatabaseManager) : ViewModel() {
  
  private var grade = MutableLiveData<String>()
  
  fun getGrade(): LiveData<String> {
    grade.value = databaseManager.getGlucoseGrade()
    return grade
  }
}