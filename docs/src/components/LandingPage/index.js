import React from 'react';
import Hero from './Hero';
import Introduction from './Introduction';
function LandingLayout(props) {
  return (
    <div className="leading-normal tracking-normal text-white">
      {/* <!--Hero part--> */}
      <Hero />

      {/* <!-- Introduction part --> */}
      <Introduction />
    </div>
  );
}

export default LandingLayout;
