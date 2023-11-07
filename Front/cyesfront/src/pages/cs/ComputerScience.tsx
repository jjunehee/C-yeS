import React, { useState } from "react";
import BottomNav from "../../components/bottomnav/BottomNav";
import RoundCornerBtn from "../../components/RoundCornerBtn";
import "./ComputerScience.css";
import { useNavigate } from "react-router-dom";
import SelectTitleModal from "../../components/modal/SelectTitleModal";
type Props = {};

const ComputerScience = (props: Props) => {
  const navigate = useNavigate();
  const enterCardStudy = () => {
    navigate("/cs/card");
  };

  const [isModalOpen, setIsModalOpen] = useState(false);

  // 모달 열기
  const openModal = () => setIsModalOpen(true);

  // 문제 시작하기
  const close = () => setIsModalOpen(false);

  return (
    <div className="live-container">
      <div className="content">
        <div> cs 학습 </div>
        <div>
          <button onClick={openModal}>모달 열기</button>
          <SelectTitleModal
            isOpen={isModalOpen}
            close={close}
          ></SelectTitleModal>
        </div>
        <br />
        <div className="button-container">
          <RoundCornerBtn
            width="300px"
            height="100px"
            fontSize="30px"
            fontcolor="black"
            bgcolor="white"
            bghover="grey"
            customshadow="2px 2px 4px rgba(0, 0, 0, 0.2)"
            onClick={enterCardStudy}
          >
            <img className="" src="/icon/card.png" alt="" />
            카드 학습
          </RoundCornerBtn>

          <RoundCornerBtn
            width="300px"
            height="100px"
            fontSize="30px"
            fontcolor="black"
            bgcolor="white"
            bghover="grey"
            customshadow="2px 2px 4px rgba(0, 0, 0, 0.2)"
            onClick={enterCardStudy}
          >
            <img className="" src="/icon/select.png" alt="" />
            객관식
          </RoundCornerBtn>
          <RoundCornerBtn
            width="300px"
            height="100px"
            fontSize="30px"
            fontcolor="black"
            bgcolor="white"
            bghover="grey"
            customshadow="2px 2px 4px rgba(0, 0, 0, 0.2)"
            onClick={enterCardStudy}
          >
            <img className="" src="/icon/torf.png" alt="" />T or F
          </RoundCornerBtn>

          {/* <div>객관식</div>
          <div>T or F</div> */}
        </div>
      </div>
      <BottomNav checkCS={true} checkLive={false} checkGroup={false} />
    </div>
  );
};

export default ComputerScience;
