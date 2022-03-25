package com.project.makecake.service;

import com.project.makecake.dto.MyOrderListResponseDto;
import com.project.makecake.dto.UserOrderRequestDto;
import com.project.makecake.dto.UserOrdersDetailResponseDto;
import com.project.makecake.model.Design;
import com.project.makecake.model.OrderForm;
import com.project.makecake.model.User;
import com.project.makecake.model.UserOrders;
import com.project.makecake.repository.DesignRepository;
import com.project.makecake.repository.OrderFormRepository;
import com.project.makecake.repository.UserOrdersRepository;
import com.project.makecake.repository.UserRepository;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor

public class UserOrdersService {

    private final UserRepository userRepository;
    private final DesignRepository designRepository;
    private final UserOrdersRepository userOrdersRepository;
    private final OrderFormRepository orderFormRepository;


    // (마이페이지 > 케이크 주문) 주문 안 된/된 도안 리스트 조회 메소드
    public List<MyOrderListResponseDto> getMyOrderList(UserDetailsImpl userDetails, String option, int page) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인을 해주세요.");
        }

        User foundUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        List<MyOrderListResponseDto> responseDtoList = new ArrayList<>();
        if (option.equals("notOrdered")){
            Pageable pageable = PageRequest.of(page, 18);
            Page<Design> foundDesignList = designRepository.findByUserAndOrdersOrderByCreatedAtDesc(foundUser, false, pageable);
            for (Design design : foundDesignList){
                MyOrderListResponseDto responseDto = MyOrderListResponseDto.builder()
                        .designId(design.getDesignId())
                        .img(design.getImgUrl())
                        .build();
                responseDtoList.add(responseDto);
            }
        } else if (option.equals("ordered")){
            Pageable pageable = PageRequest.of(page, 18);
            Page<UserOrders> foundUserOrdersList = userOrdersRepository.findByUserOrderByCreatedAtDesc(foundUser, pageable);
            for (UserOrders userOrders : foundUserOrdersList){
                MyOrderListResponseDto responseDto = MyOrderListResponseDto.builder()
                        .userOrdersId(userOrders.getUserOrdersId())
                        .designId(userOrders.getDesign().getDesignId())
                        .img(userOrders.getDesign().getImgUrl())
                        .build();
                responseDtoList.add(responseDto);
            }
        }
        return responseDtoList;
    }

    // (주문하기) 케이크 주문서 작성 메소드
    @Transactional
    public HashMap<String, Long> addUserOrders(Long orderFormId, UserOrderRequestDto requestDto, UserDetailsImpl userDetails) {

        String formFilled = "";
        for(String input : requestDto.getUserInput()){
            formFilled += input + "<br>";
            System.out.println(formFilled);
        }
        System.out.println("완료 " + formFilled);

        OrderForm orderForm = orderFormRepository.findById(orderFormId)
                .orElseThrow(()-> new NullPointerException("주문서가 존재하지 않습니다."));

        Design foundDesign = designRepository.findById(requestDto.getDesignId())
                .orElseThrow(()-> new NullPointerException("도안이 존재하지 않습니다."));

        UserOrders userOrders = UserOrders.builder()
                .user(userDetails.getUser())
                .design(foundDesign)
                .orderForm(orderForm)
                .formFilled(formFilled)
                .build();

        UserOrders savedUserOrders = userOrdersRepository.save(userOrders);

        foundDesign.editOrderState(true);

        // 반환 객체 생성
        HashMap<String,Long> responseDto = new HashMap<>();
        responseDto.put("userOrdersId", savedUserOrders.getUserOrdersId());
        return responseDto;
    }



    public UserOrdersDetailResponseDto getUserOrdersDetails(long userOrdersId, UserDetailsImpl userDetails) {
        UserOrders userOrders = userOrdersRepository.findById(userOrdersId)
                .orElseThrow(()-> new NullPointerException("작성하신 주문이 존재하지 않습니다."));

        OrderForm orderForm = userOrders.getOrderForm();

        // 주문서 양식
        List<String> formList = new ArrayList<>();
        String form = orderForm.getForm();
        List<String> rawFormList = Arrays.asList(form.split(":"));

        for(String rawForm : rawFormList){
            formList.add(rawForm.trim());
        }

        System.out.println(formList.toString());

        // 주문 전 필독사항
        List<String> instructionList = new ArrayList<>();
        String instruction = orderForm.getInstruction();

        List<String> rawInstructionList = Arrays.asList(instruction.trim().split("\\*"));
        for(String rawInstruction : rawInstructionList) {
            if(!rawInstruction.trim().equals("")){
                instructionList.add(rawInstruction.trim());
            }
        }

        // 사용자 입력 사항
        List<String> userInputList = new ArrayList<>();
        String userInput = userOrders.getFormFilled();

        List<String> rawUserInputList = Arrays.asList(userInput.split("<br>"));
        for(String rawUserInput : rawUserInputList) {
            userInputList.add(rawUserInput.trim());
        }

        System.out.println(userInputList.toString());

        // copyText





        UserOrdersDetailResponseDto responseDto = UserOrdersDetailResponseDto.builder()
                .userOrders(userOrders)
                .formList(formList)
                .instructionList(instructionList)
                .userInput(userInputList)



                .build();
        /*
            @Builder
    public UserOrdersDetailResponseDto (UserOrders userOrders, List<String> formList, List<String>instructionList, List<String>userInput, String copyText, String storeUrl) {
        this.name = userOrders.getOrderForm().getName();
        this.img = userOrders.getDesign().getImgUrl();
        this.formList = formList;
        this.instructionList = instructionList;
        this.userInput = userInput;
        this.copyText = copyText;
        this.storeUrl = storeUrl;
         */

        return responseDto;

    }

}
