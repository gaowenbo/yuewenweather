webpackJsonp([16],{XeDX:function(e,i,t){"use strict";Object.defineProperty(i,"__esModule",{value:!0});var s=function(){var e=this,i=e.$createElement,t=e._self._c||i;return t("div",{ref:"wishSwiper",staticClass:"swiper-container"},[t("div",{staticClass:"swiper-wrapper swiper-no-swiping"},e._l(e.sentWish,function(i){return t("div",{key:i.id,staticClass:"swiper-slide"},[t("span",{staticClass:"wish-item",style:e.theme},[e._v(" "+e._s(i.desc)+" ")])])}),0)])};s._withStripped=!0;t("QBuC");var n={name:"wishSwiper",props:{wishData:Object,theme:Object,activePage:Number,selfPage:Number},data:function(){return{wishSwiper:null}},computed:{sentWish:function(){var e=(this.wishData||{}).gifts;return(e=e||[]).length>2&&e.length<10&&(e=this.copyDanMu(e)),(e||[]).map(function(e,i){var t=e.giver_name,s=e.price,n=e.card_gift,r=e.id,a=n||{},c=a.title,o=a.desc;return{id:r+i,desc:(c?"".concat(t," 送了 ").concat(c):t)+(c?" ".concat(o):" 送了".concat(s,"元红包"))}})}},watch:{activePage:function(e){e===this.selfPage&&this.initWishSwiper()}},methods:{copyDanMu:function(e){return(e=e.concat(e)).length<10?this.copyDanMu(e):e},initWishSwiper:function(){var e=this.sentWish.length;this.wishSwiper&&this.wishSwiper.destroy(),this.wishSwiper=new window.Swiper(this.$refs.wishSwiper,{direction:"vertical",slidesPerView:"auto",loopedSlides:e,loop:e>2,speed:2500,freeMode:!0,autoplay:e>2&&{delay:1}})}},mounted:function(){this.initWishSwiper()}},r=(t("i8Nm"),t("K1/g")),a=Object(r.a)(n,s,[],!1,null,"3011ab02",null);a.options.__file="src/new-card/views/index/pages/gift/wishSwiper/index.vue";i.default=a.exports},i8Nm:function(e,i,t){"use strict";var s=t("vYSq");t.n(s).a},vYSq:function(e,i){}});