
<template>
  <div class="monitor-wrapper">
    <div class="info-wrapper"></div>
    <div class="search-wrapper"></div>
    <div class="center-chart-wrapper"></div>
    <div class="bottom-chart-wraper"></div>
  </div>
</template>
<script></script>
<style lang="scss" scoped>
.monitor-wrapper {
  width: 100%;
  min-height: calc(100vh - 50px);
  display: grid;
  grid-template-rows: 60px 1fr 2fr;
  grid-template-columns: 300px 2fr 1fr;
  background-color: #ebebf3;
  gap: 10px;
  padding: 10px;
  > div {
    border-radius: 8px;
    background-color: #fff;
  }
  .info-wrapper {
    grid-row: 1 / 4;
    grid-column: 1 / 2;
  }
  .search-wrapper {
    grid-row: 1 / 2;
    grid-column: 2 / 4;
  }
  .center-chart-wrapper {
    grid-row: 2 / 3;
    grid-column: 2 / 4;
  }
  .bottom-chart-wraper {
    grid-row: 3 / 4;
    grid-column: 2 / 4;
  }
}
</style>
