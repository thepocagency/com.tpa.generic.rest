/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tpa.generic.rest.mongodbbased.generic.data.bean;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

public class Pagination {

    private Long pageIndex;
    private Long numberOfPages;
    private Long totalItemCount;
    private Long pageSize;

    public Pagination(Long totalItemCount, Long pageSize) {
        this.pageIndex = new Long(0);
        this.totalItemCount = totalItemCount;
        
        Float left = (float) totalItemCount % pageSize;
        if (left == 0) {
            this.numberOfPages = totalItemCount / pageSize;
        }
        else {
            this.numberOfPages = (totalItemCount / pageSize) + 1;
        }
        
        this.pageSize = pageSize;
    }

    public Long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Long getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Long numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public Long getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(Long totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
