package ru.practicum.shareit.request;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class RequestMapper {

    public Request requestCreateDtoToRequest(RequestCreateDto requestCreateDto) {
        Request request = new Request();
        request.setDescription(requestCreateDto.getDescription());
        return request;
    }

    public RequestDto requestToRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setCreated(request.getCreated());
        requestDto.setDescription(request.getDescription());
        requestDto.setId(request.getId());
        return requestDto;
    }


}
